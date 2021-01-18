package subway.line;

import org.springframework.stereotype.Service;
import subway.exceptions.InvalidSectionException;
import subway.station.StationDao;
import subway.station.StationResponse;

import java.util.ArrayList;
import java.util.List;

@Service
public class LineService {

    private LineDao lineDao;
    private SectionDao sectionDao;
    private StationDao stationDao;

    public LineService(LineDao lineDao, SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public Line save(LineRequest lineRequest) {
        lineRequest.validateLineRequest();
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
        List<Section> sections = sectionDao.findAllSections(id, lineDao.findById(id).getStartStationId());
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
        if (line.isLineStartStation(sectionRequest.getDownStationId())) {
            return saveSectionsHead(lineId, sectionRequest);
        }
        if (line.isLineEndStation(sectionRequest.getUpStationId())) {
            return saveSectionsTail(lineId, sectionRequest);
        }
        return saveBetweenSections(lineId, sectionRequest);
    }

    private boolean isContainsBothStationsOrNothing(Long lineId, Long upStationId, Long downStationId) {
        int upStationCount = sectionDao.countByLineIdAndStationId(lineId, upStationId);
        int downStationCount = sectionDao.countByLineIdAndStationId(lineId, downStationId);
        return ((upStationCount > 0) == (downStationCount > 0));
    }

    private Line saveSectionsHead(Long lineId, SectionRequest sectionRequest) {
        lineDao.updateLineStartStation(lineId, sectionRequest.getUpStationId());
        sectionDao.save(lineId, sectionRequest);
        return lineDao.findById(lineId);
    }

    private Line saveSectionsTail(Long lineId, SectionRequest sectionRequest) {
        lineDao.updateLineEndStation(lineId, sectionRequest.getDownStationId());
        sectionDao.save(lineId, sectionRequest);
        return lineDao.findById(lineId);
    }

    private Line saveBetweenSections(Long lineId, SectionRequest sectionRequest) {
        Section newSection = updateSection(lineId, sectionRequest);
        sectionDao.updateSection(newSection);
        sectionDao.save(lineId, sectionRequest);
        return lineDao.findById(lineId);
    }

    private Section updateSection(Long lineId, SectionRequest sectionRequest) {
        Long sectionId = sectionDao.findSectionIdFromEqualUpStationId(lineId, sectionRequest.getUpStationId());
        if (sectionId == 0L) {
            sectionId = sectionDao.findSectionIdFromEqualDownStationId(lineId, sectionRequest.getDownStationId());
            Section section = sectionDao.findById(sectionId);
            return new Section(sectionId, lineId, section.getUpStationId(), sectionRequest.getUpStationId(),
                    section.getDistance() - sectionRequest.getDistance());
        }
        Section section = sectionDao.findById(sectionId);
        return new Section(sectionId, lineId, sectionRequest.getDownStationId(), section.getDownStationId(),
                section.getDistance() - sectionRequest.getDistance());
    }

    public void deleteStationById(Long lineId, Long stationId) {
        if (isLineContainsOnlyOneSection(lineId)) {
            throw new InvalidSectionException("구간이 하나이기 때문에 삭제할 수 없습니다.");
        }
        Line line = lineDao.findById(lineId);
        if (line.isLineStartStation(stationId)) {
            deleteStartStation(lineId, stationId);
            return;
        }
        if (line.isLineEndStation(stationId)) {
            deleteEndStation(lineId, stationId);
            return;
        }
        sectionDao.deleteById(lineId, stationId);
    }

    private boolean isLineContainsOnlyOneSection(long lineId) {
        return sectionDao.countByLineId(lineId) == 1;
    }

    private void deleteStartStation(Long lineId, Long stationId) {
        Section section = sectionDao.findById(sectionDao.findSectionIdFromEqualUpStationId(lineId, stationId));
        lineDao.updateLineStartStation(lineId, section.getDownStationId());
        sectionDao.deleteByLineIdAndUpStationId(lineId, stationId);
    }

    private void deleteEndStation(Long lineId, Long stationId) {
        Section section = sectionDao.findById(sectionDao.findSectionIdFromEqualDownStationId(lineId, stationId));
        lineDao.updateLineEndStation(lineId, section.getUpStationId());
        sectionDao.deleteByLineIdAndDownStationId(lineId, stationId);
    }
}
