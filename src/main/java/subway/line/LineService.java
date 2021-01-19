package subway.line;

import org.springframework.stereotype.Service;
import subway.exceptions.DuplicateLineNameException;
import subway.exceptions.EmptySectionException;
import subway.exceptions.InvalidSectionException;
import subway.section.Section;
import subway.section.SectionDao;
import subway.section.SectionRequest;
import subway.station.Station;
import subway.station.StationDao;
import subway.station.StationResponse;

import java.util.ArrayList;
import java.util.List;

@Service
public class LineService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public LineService(LineDao lineDao, SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public Line save(LineRequest lineRequest) {
        lineRequest.validateLineRequest();
        validateDuplicateLineName(lineRequest.getName());
        long lineId = lineDao.save(lineRequest);
        sectionDao.save(lineId, new SectionRequest(lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance()));
        return findById(lineId);
    }

    private void validateDuplicateLineName(String name) {
        if (lineDao.countByName(name) > 0) {
            throw new DuplicateLineNameException("중복된 노선 이름입니다.");
        }
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public Line findById(long id) {
        return lineDao.findById(id);
    }

    public boolean deleteById(long id) {
        return (lineDao.deleteById(id) == 1) && (sectionDao.deleteAllByLineId(id) > 0);
    }

    public Line updateLine(long id, LineRequest lineRequest) {
        return lineDao.updateLine(id, lineRequest);
    }

    public List<Station> getStationsById(long id) {
        List<Station> stations = new ArrayList<>();
        List<Section> sections = sectionDao.findAllSections(id, lineDao.findById(id).getStartStationId());
        if (sections.size() == 0) {
            throw new EmptySectionException("라인 내에 구간이 존재하지 않습니다.");
        }
        for (Section section : sections) {
            stations.add(new Station(section.getUpStationId(), stationDao.findById(section.getUpStationId()).getName()));
        }
        long endStationId = sections.get(sections.size() - 1).getDownStationId();
        stations.add(new Station(endStationId, stationDao.findById(endStationId).getName()));
        return stations;
    }

    public Line saveSection(long lineId, SectionRequest sectionRequest) {
        validateAlreadyExistBothStationsOrNothing(lineId, sectionRequest);
        Line line = lineDao.findById(lineId);
        if (line.isLineStartStation(sectionRequest.getDownStationId())) {
            return saveSectionsHead(lineId, sectionRequest);
        }
        if (line.isLineEndStation(sectionRequest.getUpStationId())) {
            return saveSectionsTail(lineId, sectionRequest);
        }
        return saveBetweenSections(lineId, sectionRequest);
    }

    private void validateAlreadyExistBothStationsOrNothing(long lineId, SectionRequest sectionRequest) {
        int upStationCount = sectionDao.countByLineIdAndStationId(lineId, sectionRequest.getUpStationId());
        int downStationCount = sectionDao.countByLineIdAndStationId(lineId, sectionRequest.getDownStationId());
        if ((upStationCount > 0) == (downStationCount > 0)) {
            throw new InvalidSectionException("두 역 모두 이미 존재하거나, 모두 포함되어 있지 않습니다.");
        }
    }

    private Line saveSectionsHead(long lineId, SectionRequest sectionRequest) {
        lineDao.updateLineStartStation(lineId, sectionRequest.getUpStationId());
        sectionDao.save(lineId, sectionRequest);
        return lineDao.findById(lineId);
    }

    private Line saveSectionsTail(long lineId, SectionRequest sectionRequest) {
        lineDao.updateLineEndStation(lineId, sectionRequest.getDownStationId());
        sectionDao.save(lineId, sectionRequest);
        return lineDao.findById(lineId);
    }

    private Line saveBetweenSections(long lineId, SectionRequest sectionRequest) {
        Section newSection = makeUpdatedSection(lineId, sectionRequest);
        sectionDao.updateSection(newSection);
        sectionDao.save(lineId, sectionRequest);
        return lineDao.findById(lineId);
    }

    private Section makeUpdatedSection(long lineId, SectionRequest sectionRequest) {
        long sectionId = sectionDao.findSectionIdByUpStationId(lineId, sectionRequest.getUpStationId());
        if (sectionId == 0L) {
            sectionId = sectionDao.findSectionIdByDownStationId(lineId, sectionRequest.getDownStationId());
            Section section = sectionDao.findById(sectionId);
            return new Section(sectionId, lineId, section.getUpStationId(), sectionRequest.getUpStationId(),
                    section.getDistance() - sectionRequest.getDistance());
        }
        Section section = sectionDao.findById(sectionId);
        return new Section(sectionId, lineId, sectionRequest.getDownStationId(), section.getDownStationId(),
                section.getDistance() - sectionRequest.getDistance());
    }

    public void deleteStationById(long lineId, long stationId) {
        validateLineContainsOnlyOneSection(lineId);
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

    private void validateLineContainsOnlyOneSection(long lineId) {
        if (sectionDao.countByLineId(lineId) == 1) {
            throw new InvalidSectionException("구간이 하나이기 때문에 삭제할 수 없습니다.");
        }
    }

    private void deleteStartStation(long lineId, long stationId) {
        Section section = sectionDao.findById(sectionDao.findSectionIdByUpStationId(lineId, stationId));
        lineDao.updateLineStartStation(lineId, section.getDownStationId());
        sectionDao.deleteByLineIdAndUpStationId(lineId, stationId);
    }

    private void deleteEndStation(long lineId, long stationId) {
        Section section = sectionDao.findById(sectionDao.findSectionIdByDownStationId(lineId, stationId));
        lineDao.updateLineEndStation(lineId, section.getUpStationId());
        sectionDao.deleteByLineIdAndDownStationId(lineId, stationId);
    }
}
