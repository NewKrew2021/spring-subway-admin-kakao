package subway.line;

import org.springframework.stereotype.Service;
import subway.exceptions.InvalidSectionException;
import subway.section.Section;
import subway.section.SectionDao;
import subway.section.SectionRequest;
import subway.section.Sections;
import subway.station.StationDao;
import subway.station.StationResponse;

import java.util.ArrayList;
import java.util.List;

@Service
public class LineService {

    public static final String CONTAINS_BOTH_STATIONS_OR_NOT_EXISTS_STATIONS_ERROR_MESSAGE = "두 역이 모두 포함되어 있거나, 두 역 모두 포함되어 있지 않습니다.";
    public static final String SECTIONS_SIZE_ERROR_MESSAGE = "구간이 하나이기 때문에 삭제할 수 없습니다.";
    private LineDao lineDao;
    private SectionDao sectionDao;
    private StationDao stationDao;

    public LineService(LineDao lineDao, SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public Line save(LineRequest lineRequest) {
        lineRequest.checkLineRequest();
        Line line = new Line(lineRequest.getName(), lineRequest.getColor(), lineRequest.getUpStationId(), lineRequest.getDownStationId());
        Line newLine = lineDao.save(line);
        Section section = new Section(newLine.getId(), lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance());
        sectionDao.save(section);
        return findById(newLine.getId());
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
        return lineDao.updateLineNameAndColor(new Line(id, lineRequest.getName(), lineRequest.getColor()));
    }

    public List<StationResponse> getStationResponsesById(Long lineId) {
        List<StationResponse> responses = new ArrayList<>();
        Line line = lineDao.findById(lineId);
        Sections sections = new Sections(sectionDao.findAllSectionsByLineId(line.getId()), line.getStartStationId());
        for (Long stationId : sections.getStationsSortedSequence()) {
            responses.add(new StationResponse(stationDao.findById(stationId)));
        }
        return responses;
    }

    public Line saveSection(Long lineId, SectionRequest sectionRequest) {
        Line line = lineDao.findById(lineId);
        Sections sections = new Sections(sectionDao.findAllSectionsByLineId(lineId), line.getStartStationId());
        if (sections.isContainsBothStationsOrNothing(sectionRequest.getUpStationId(), sectionRequest.getDownStationId())) {
            throw new InvalidSectionException(CONTAINS_BOTH_STATIONS_OR_NOT_EXISTS_STATIONS_ERROR_MESSAGE);
        }
        Section newSection = new Section(lineId, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());
        if (line.isLineStartStation(sectionRequest.getDownStationId())) {
            return saveSectionsHead(newSection, line);
        }
        if (line.isLineEndStation(sectionRequest.getUpStationId())) {
            return saveSectionsTail(newSection, line);
        }
        return saveBetweenSections(sections, newSection);
    }

    private Line saveSectionsHead(Section newSection, Line line) {
        Line newLine = new Line(line.getId(), newSection.getUpStationId(), line.getEndStationId());
        lineDao.updateLineStartEndStations(newLine);
        sectionDao.save(newSection);
        return lineDao.findById(newSection.getLineId());
    }

    private Line saveSectionsTail(Section newSection, Line line) {
        Line newLine = new Line(line.getId(), line.getStartStationId(), newSection.getDownStationId());
        lineDao.updateLineStartEndStations(newLine);
        sectionDao.save(newSection);
        return lineDao.findById(newSection.getLineId());
    }

    private Line saveBetweenSections(Sections sections, Section newSection) {
        Section updatedSection = sections.findUpdatedSection(newSection);
        sectionDao.updateById(updatedSection);
        sectionDao.save(newSection);
        return lineDao.findById(newSection.getLineId());
    }

    public void deleteStationById(Long lineId, Long stationId) {
        Line line = lineDao.findById(lineId);
        Sections sections = new Sections(sectionDao.findAllSectionsByLineId(lineId), line.getStartStationId());
        if (sections.getSize() == 1) {
            throw new InvalidSectionException(SECTIONS_SIZE_ERROR_MESSAGE);
        }
        if (line.isLineStartStation(stationId)) {
            deleteStartStation(sections, stationId, line);
            return;
        }
        if (line.isLineEndStation(stationId)) {
            deleteEndStation(sections, stationId, line);
            return;
        }
        mergeAndDeleteStation(sections, stationId);
    }

    private void mergeAndDeleteStation(Sections sections, Long stationId) {
        Section deletedSection = sections.findByUpStationId(stationId);
        Section updatedSection = sections.findByDownStationId(stationId);
        sectionDao.updateById(new Section(updatedSection.getId(), updatedSection.getLineId(), updatedSection.getUpStationId(),
                deletedSection.getDownStationId(), updatedSection.getDistance() + deletedSection.getDistance()));
        sectionDao.deleteById(deletedSection.getId());
    }

    private void deleteStartStation(Sections sections, Long stationId, Line line) {
        Section section = sections.findByUpStationId(stationId);
        Line newLine = new Line(line.getId(), section.getDownStationId(), line.getEndStationId());
        lineDao.updateLineStartEndStations(newLine);
        sectionDao.deleteById(section.getId());
    }

    private void deleteEndStation(Sections sections, Long stationId, Line line) {
        Section section = sections.findByDownStationId(stationId);
        Line newLine = new Line(line.getId(), line.getStartStationId(), section.getUpStationId());
        lineDao.updateLineStartEndStations(newLine);
        sectionDao.deleteById(section.getId());
    }
}
