package subway.line;

import org.springframework.stereotype.Service;
import subway.exception.exceptions.DuplicateLineNameException;
import subway.exception.exceptions.FailedDeleteLineException;
import subway.section.Section;
import subway.section.SectionDao;
import subway.section.SectionRequest;
import subway.section.Sections;
import subway.station.Station;
import subway.station.StationDao;

import java.util.ArrayList;
import java.util.List;

@Service
public class LineService {

    private static final int MIN_DUPLICATE_LINE_NAME_COUNT = 1;

    private static final String DUPLICATE_LINE_NAME_MESSAGE = "중복된 노선 이름입니다.";
    private static final String FAIL_DELETE_LINE_MESSAGE = "노선을 삭제할 수 없습니다.";
    private static final String FAIL_DELETE_SECTIONS_MESSAGE = "노선 내 모든 구간 정보를 삭제할 수 없습니다.";

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
        long lineId = lineDao.save(lineRequest.toLine());
        sectionDao.save(lineId, lineRequest.toSection());
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

    public void deleteById(long id) {
        if (lineDao.deleteById(id) != 1) {
            throw new FailedDeleteLineException(FAIL_DELETE_LINE_MESSAGE);
        }
        if (sectionDao.deleteAllByLineId(id) <= 0) {
            throw new FailedDeleteLineException(FAIL_DELETE_SECTIONS_MESSAGE);
        }
    }

    public Line updateLine(long id, LineRequest lineRequest) {
        return lineDao.updateLine(id, lineRequest.toLine());
    }

    public List<Station> getStationsById(long lineId) {
        Sections sections = new Sections(sectionDao.findByLineId(lineId));
        long stationId = lineDao.findById(lineId).getStartStationId();
        List<Station> stations = new ArrayList<>();
        for (int i = 0; i < sections.size(); i++) {
            stations.add(stationDao.findById(stationId));
            stationId = sections.nextUpStationId(stationId);
        }
        stations.add(stationDao.findById(stationId));
        return stations;
    }

    public Line saveSection(long lineId, SectionRequest sectionRequest) {
        Sections sections = new Sections(sectionDao.findByLineId(lineId));
        Section newSection = sectionRequest.toSection();
        sections.validateAlreadyExistBothStationsOrNothing(newSection);

        Line line = lineDao.findById(lineId);
        if (line.isLineStartStation(newSection.getDownStationId())) {
            return saveSectionsHead(lineId, newSection);
        }
        if (line.isLineEndStation(newSection.getUpStationId())) {
            return saveSectionsTail(lineId, newSection);
        }
        return saveBetweenSections(lineId, newSection);
    }

    private Line saveSectionsHead(long lineId, Section newSection) {
        lineDao.updateLineStartStation(lineId, newSection.getUpStationId());
        sectionDao.save(lineId, newSection);
        return lineDao.findById(lineId);
    }

    private Line saveSectionsTail(long lineId, Section newSection) {
        lineDao.updateLineEndStation(lineId, newSection.getDownStationId());
        sectionDao.save(lineId, newSection);
        return lineDao.findById(lineId);
    }

    private Line saveBetweenSections(long lineId, Section newSection) {
        Section updatedSection = makeUpdateCandidateSection(lineId, newSection);
        sectionDao.updateSection(updatedSection);
        sectionDao.save(lineId, newSection);
        return lineDao.findById(lineId);
    }

    private Section makeUpdateCandidateSection(long lineId, Section newSection) {
        Sections sections = new Sections(sectionDao.findByLineId(lineId));
        return sections.getUpdatedSection(newSection);
    }

    public void deleteStationById(long lineId, long stationId) {
        Sections sections = new Sections(sectionDao.findByLineId(lineId));
        sections.validateLineContainsOnlyOneSection();

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

    private void deleteStartStation(long lineId, long stationId) {
        Sections sections = new Sections(sectionDao.findByLineId(lineId));
        Section section = sections.findSectionByUpStationId(stationId);
        lineDao.updateLineStartStation(lineId, section.getDownStationId());
        sectionDao.deleteByLineIdAndUpStationId(lineId, stationId);
    }

    private void deleteEndStation(long lineId, long stationId) {
        Sections sections = new Sections(sectionDao.findByLineId(lineId));
        Section section = sections.findSectionByDownStationId(stationId);
        lineDao.updateLineEndStation(lineId, section.getUpStationId());
        sectionDao.deleteByLineIdAndDownStationId(lineId, stationId);
    }

    private void deleteInterStation(long lineId, long stationId) {
        Sections sections = new Sections(sectionDao.findByLineId(lineId));
        Section surviveDownStationSection = sections.findSectionByUpStationId(stationId);
        Section surviveUpStationSection = sections.findSectionByDownStationId(stationId);

        sectionDao.deleteByLineIdAndUpStationId(lineId, stationId);
        sectionDao.deleteByLineIdAndDownStationId(lineId, stationId);
        sectionDao.save(lineId, new Section(
                surviveUpStationSection.getUpStationId(),
                surviveDownStationSection.getDownStationId(),
                surviveDownStationSection.getDistance() + surviveUpStationSection.getDistance()
        ));
    }
}
