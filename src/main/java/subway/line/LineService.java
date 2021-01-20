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
        Line newLine = lineDao.save(lineRequest.toLine());
        Section section = lineRequest.toSection(newLine.getId());
        sectionDao.save(section);
        return findById(newLine.getId());
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public Line findById(Long id) {
        return lineDao.findById(id);
    }

    public void deleteById(Long id) {
         lineDao.deleteById(id);
         sectionDao.deleteAllByLineId(id);
    }

    public Line updateLine(Long id, LineRequest lineRequest) {
        Line line = lineDao.findById(id);
        line.updateLineInfo(lineRequest);
        return lineDao.updateLine(line);
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
        Section newSection = sectionRequest.toSection(line.getId());

        if(line.isStartStation(newSection.getDownStationId()) || line.isEndStation(newSection.getUpStationId())) {
            return saveSectionsHeadOrTail(newSection, line);
        }
        return saveBetweenSections(sections, newSection);
    }

    private Line saveSectionsHeadOrTail(Section newSection, Line line) {
        line.updateStationInfoWhenInserted(newSection);
        lineDao.updateLine(line);
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
        if (!sections.isRemovable()) {
            throw new InvalidSectionException(SECTIONS_SIZE_ERROR_MESSAGE);
        }
        if(line.isStartStation(stationId) || line.isEndStation(stationId)) {
            deleteStartOrEndStation(sections, stationId, line);
            return;
        }
        mergeAndDeleteStation(sections, stationId);
    }

    private void mergeAndDeleteStation(Sections sections, Long stationId) {
        Section deletedSection = sections.findByUpStationId(stationId);
        Section updatedSection = sections.findByDownStationId(stationId);
        updatedSection.updateSectionInfoWhenDeleted(deletedSection);
        sectionDao.updateById(updatedSection);
        sectionDao.deleteById(deletedSection.getId());
    }

    private void deleteStartOrEndStation(Sections sections, Long stationId, Line line) {
        Section section = getStartOrEndSection(sections, stationId, line);
        line.updateStationInfoWhenDeleted(section);
        lineDao.updateLine(line);
        sectionDao.deleteById(section.getId());
    }

    private Section getStartOrEndSection(Sections sections, Long stationId, Line line) {
        if(line.isStartStation(stationId)) {
            return sections.getFirstSection();
        }
        return sections.getLastSection();
    }
}
