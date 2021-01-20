package subway.line;

import org.springframework.stereotype.Service;
import subway.exception.exceptions.DuplicateLineNameException;
import subway.exception.exceptions.FailedSaveSectionException;
import subway.exception.exceptions.InvalidSectionException;
import subway.section.Section;
import subway.section.SectionDao;
import subway.section.SectionRequest;
import subway.station.Station;
import subway.station.StationDao;

import java.util.ArrayList;
import java.util.List;

@Service
public class LineService {

    private static final int MIN_DUPLICATE_LINE_NAME_COUNT = 1;
    private static final long MIN_NECESSARY_SECTION_COUNT = 1;

    private static final String DUPLICATE_LINE_NAME_MESSAGE = "중복된 노선 이름입니다.";
    private static final String UNABLE_STATION_MESSAGE = "두 역 모두 이미 존재하거나, 모두 포함되어 있지 않습니다.";
    private static final String NOT_INCLUDED_STATION_MESSAGE = "두 역 모두 해당 노선에 포함된 역이 아닙니다.";
    private static final String ALONE_SECTION_MESSAGE = "구간이 하나이기 때문에 삭제할 수 없습니다.";

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
        sectionDao.save(lineId, SectionRequest.of(lineRequest));
        return findById(lineId);
    }

    private void validateDuplicateLineName(String name) {
        if (lineDao.countByName(name) >= MIN_DUPLICATE_LINE_NAME_COUNT) {
            throw new DuplicateLineNameException(DUPLICATE_LINE_NAME_MESSAGE);
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

    public List<Station> getStationsById(long lineId) {
        List<Section> sections = sectionDao.findByLineId(lineId);
        List<Station> stations = new ArrayList<>();
        long stationId = lineDao.findById(lineId).getStartStationId();
        int sectionsCount = sections.size();
        for (int i = 0; i < sectionsCount; i++) {
            stations.add(stationDao.findById(stationId));
            for (Section section : sections) {
                if (section.getLineId() == lineId && section.getUpStationId() == stationId) {
                    stationId = section.getDownStationId();
                    break;
                }
            }
        }
        stations.add(stationDao.findById(stationId));
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
        List<Section> sections = sectionDao.findByLineId(lineId);
        long upStationCount = sections.stream()
                .filter(section -> (section.getUpStationId() == sectionRequest.getUpStationId() || section.getDownStationId() == sectionRequest.getUpStationId()))
                .count();
        long downStationCount = sections.stream()
                .filter(section -> (section.getUpStationId() == sectionRequest.getDownStationId() || section.getDownStationId() == sectionRequest.getDownStationId()))
                .count();
        if ((upStationCount > 0) == (downStationCount > 0)) {
            throw new InvalidSectionException(UNABLE_STATION_MESSAGE);
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
        List<Section> sections = sectionDao.findByLineId(lineId);
        for (Section section : sections) {
            if (section.getUpStationId() == sectionRequest.getUpStationId()) {
                section.updateUpStationAndDistance(sectionRequest.getDownStationId(), sectionRequest.getDistance());
                return section;
            }
        }
        for (Section section : sections) {
            if (section.getDownStationId() == sectionRequest.getDownStationId()) {
                section.updateDownStationAndDistance(sectionRequest.getUpStationId(), sectionRequest.getDistance());
                return section;
            }
        }
        throw new FailedSaveSectionException(NOT_INCLUDED_STATION_MESSAGE);
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
        deleteInterStation(lineId, stationId);
    }

    private void validateLineContainsOnlyOneSection(long lineId) {
        List<Section> sections = sectionDao.findByLineId(lineId);
        if (sections.size() == MIN_NECESSARY_SECTION_COUNT) {
            throw new InvalidSectionException(ALONE_SECTION_MESSAGE);
        }
    }

    private void deleteStartStation(long lineId, long stationId) {
        List<Section> sections = sectionDao.findByLineId(lineId);
        for (Section section : sections) {
            if (section.getUpStationId() == stationId) {
                lineDao.updateLineStartStation(lineId, section.getDownStationId());
                sectionDao.deleteByLineIdAndUpStationId(lineId, stationId);
                return;
            }
        }
    }

    private void deleteEndStation(long lineId, long stationId) {
        List<Section> sections = sectionDao.findByLineId(lineId);
        for (Section section : sections) {
            if (section.getDownStationId() == stationId) {
                lineDao.updateLineEndStation(lineId, section.getUpStationId());
                sectionDao.deleteByLineIdAndDownStationId(lineId, stationId);
                return;
            }
        }
    }

    private void deleteInterStation(long lineId, long stationId) {
        List<Section> sections = sectionDao.findByLineId(lineId);
        Section upStationSection = sections.stream()
                .filter(section -> section.getUpStationId() == stationId)
                .findFirst().get();
        Section downStationSection = sections.stream()
                .filter(section -> section.getDownStationId() == stationId)
                .findFirst().get();
        sectionDao.deleteByLineIdAndUpStationId(lineId, stationId);
        sectionDao.deleteByLineIdAndDownStationId(lineId, stationId);
        sectionDao.save(lineId, new SectionRequest(downStationSection.getUpStationId(), upStationSection.getDownStationId(),
                upStationSection.getDistance() + downStationSection.getDistance()));
    }
}
