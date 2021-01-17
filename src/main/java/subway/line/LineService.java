package subway.line;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import subway.exceptions.InvalidSectionException;
import subway.station.StationDao;
import subway.station.StationResponse;

import java.util.ArrayList;
import java.util.List;

@Service
public class LineService {
    @Autowired
    private LineDao lineDao;
    @Autowired
    private SectionDao sectionDao;
    @Autowired
    private StationDao stationDao;

    public Line save(LineRequest lineRequest) {
        Long lineId = lineDao.save(lineRequest);
        sectionDao.save(lineId, new SectionRequest(lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance()));
        return findById(lineId);
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public Line findById(Long id) {
        return lineDao.findById(id);
    }

    public boolean deleteById(Long id) {
        return (lineDao.deleteById(id) == 1) && (sectionDao.deleteAllByLineId(id) > 0);
    }

    public Line updateLine(Long id, LineRequest lineRequest) {
        return lineDao.updateLine(id, lineRequest);
    }

    public List<StationResponse> getStationResponsesById(Long id) {
        List<StationResponse> responses = new ArrayList<>();
        Line line = findById(id);
        List<Section> sections = sectionDao.findAllSections(id, line.getStartStationId());
        for (Section section : sections) {
            responses.add(new StationResponse(section.getUpStationId(), stationDao.findById(section.getUpStationId()).getName()));
        }
        Long endStationId = sections.get(sections.size()-1).getDownStationId();
        responses.add(new StationResponse(endStationId, stationDao.findById(endStationId).getName()));
        return responses;
    }

    public Line saveSection(Long lineId, SectionRequest sectionRequest) {
        if(isContainsBothStationsOrNothing(lineId, sectionRequest.getUpStationId(), sectionRequest.getDownStationId())) {
            throw new InvalidSectionException("두 역이 모두 포함되어 있거나, 두 역 모두 포함되어 있지 않습니다.");
        }

        Line line = lineDao.findById(lineId);
        if(line.getStartStationId() == sectionRequest.getDownStationId()) {
            // update LINE set start_station_id = sectionRequest.getUpStationId();
            lineDao.updateLineStartStation(lineId, sectionRequest.getUpStationId());
            sectionDao.save(lineId, sectionRequest);
            return lineDao.findById(lineId);
        }
        if(line.getEndStationId() == sectionRequest.getUpStationId()) {
            // update LINE set end_station_id = sectionRequest.getDownStationId();
            lineDao.updateLineEndStation(lineId, sectionRequest.getDownStationId());
            sectionDao.save(lineId, sectionRequest);
            return lineDao.findById(lineId);
        }

        Long sectionId = sectionDao.findSectionIdFromEqualUpStationId(lineId, sectionRequest.getUpStationId());
        if (sectionId == 0L) {
            sectionId = sectionDao.findSectionIdFromEqualDownStationId(lineId, sectionRequest.getDownStationId());
            validateSectionDistance(sectionRequest, sectionDao.findDistanceById(sectionId));
            sectionDao.updateDownStation(sectionId, sectionRequest.getUpStationId());
            sectionDao.updateDistance(sectionId, sectionDao.findDistanceById(sectionId) - sectionRequest.getDistance());
            sectionDao.save(lineId, sectionRequest);
            return lineDao.findById(lineId);
        }
        validateSectionDistance(sectionRequest, sectionDao.findDistanceById(sectionId));
        sectionDao.updateUpStation(sectionId, sectionRequest.getDownStationId());
        sectionDao.updateDistance(sectionId, sectionDao.findDistanceById(sectionId) - sectionRequest.getDistance());
        sectionDao.save(lineId, sectionRequest);
        return lineDao.findById(lineId);
    }

    private boolean isContainsBothStationsOrNothing(Long lineId, Long upStationId, Long downStationId) {
        // select * from SECTION where line_id = lineId and ( up_station_id = stationId or down_station_id = stationId);
        int upStationCount = sectionDao.countByLineIdAndStationId(lineId, upStationId);
        int downStationCount = sectionDao.countByLineIdAndStationId(lineId, downStationId);

        // 둘다 있거나 둘다 없는 경우 return true;
        return ((upStationCount > 0) == (downStationCount > 0));
    }

    private void validateSectionDistance(SectionRequest newSection, int distance) {
        if (distance <= newSection.getDistance()) {
            throw new InvalidSectionException("추가될 구간의 거리가 기존 노선 거리보다 깁니다.");
        }
    }

    public void deleteStationById(Long lineId, Long stationId) {
        if(isLineContainsMoreThanOneSection(lineId)) {
            Line line = lineDao.findById(lineId);
            if (line.getStartStationId() == stationId) {
                Section section = sectionDao.findById(sectionDao.findSectionIdFromEqualUpStationId(lineId, stationId));
                lineDao.updateLineStartStation(lineId, section.getDownStationId());
                sectionDao.deleteByLineIdAndUpStationId(lineId, stationId);
                return;
            }
            if (line.getEndStationId() == stationId) {
                Section section = sectionDao.findById(sectionDao.findSectionIdFromEqualDownStationId(lineId, stationId));
                lineDao.updateLineEndStation(lineId, section.getUpStationId());
                sectionDao.deleteByLineIdAndDownStationId(lineId, stationId);
                return;
            }
            sectionDao.deleteById(lineId, stationId);
        }
    }

    private boolean isLineContainsMoreThanOneSection(long lineId) {
        int count = sectionDao.countByLineId(lineId);
        if (count == 1) {
            throw new InvalidSectionException("구간이 하나이기 때문에 삭제할 수 없습니다.");
        }
        return true;
    }
}
