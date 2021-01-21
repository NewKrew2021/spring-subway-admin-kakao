package subway.section;

import org.springframework.stereotype.Service;
import subway.exceptions.InvalidSectionException;
import subway.line.Line;
import subway.station.StationDao;
import subway.station.StationResponse;

import java.util.ArrayList;
import java.util.List;

@Service
public class SectionService {

    public static final String CONTAINS_BOTH_STATIONS_OR_NOT_EXISTS_STATIONS_ERROR_MESSAGE = "두 역이 모두 포함되어 있거나, 두 역 모두 포함되어 있지 않습니다.";
    public static final String SECTIONS_SIZE_ERROR_MESSAGE = "구간이 하나이기 때문에 삭제할 수 없습니다.";

    private SectionDao sectionDao;
    private StationDao stationDao;

    public SectionService(SectionDao sectionDao, StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public void save(Section section) {
        sectionDao.save(section);
    }

    public void save(Line line, SectionRequest sectionRequest) {
        Sections sections = new Sections(sectionDao.findAllSectionsByLineId(line.getId()), line.getStartStationId());
        if (sections.isContainsBothStationsOrNothing(sectionRequest.getUpStationId(), sectionRequest.getDownStationId())) {
            throw new InvalidSectionException(CONTAINS_BOTH_STATIONS_OR_NOT_EXISTS_STATIONS_ERROR_MESSAGE);
        }
        Section newSection = sectionRequest.toSection(line.getId());
        Section updatedSection = sections.findUpdatedSection(newSection);
        sectionDao.updateById(updatedSection);
        sectionDao.save(newSection);
    }

    public void deleteAllByLineId(Long lineId) {
        sectionDao.deleteAllByLineId(lineId);
    }

    public List<StationResponse> getStationResponsesById(Long lineId, Long startStationId) {
        List<StationResponse> responses = new ArrayList<>();
        Sections sections = new Sections(sectionDao.findAllSectionsByLineId(lineId), startStationId);
        for (Long stationId : sections.getStationsSortedSequence()) {
            responses.add(new StationResponse(stationDao.findById(stationId)));
        }
        return responses;
    }

    public List<Section> getSectionsByLineId(Long lineId) {
        return sectionDao.findAllSectionsByLineId(lineId);
    }

    public Section deleteSectionByStationId(Line line, Long stationId) {
        Sections sections = new Sections(sectionDao.findAllSectionsByLineId(line.getId()), line.getStartStationId());
        if (!sections.isRemovable()) {
            throw new InvalidSectionException(SECTIONS_SIZE_ERROR_MESSAGE);
        }
        if (line.isStartStation(stationId) || line.isEndStation(stationId)) {
            return deleteStartOrEndStation(sections, stationId, line);
        }
        return mergeAndDeleteStation(sections, stationId);
    }

    private Section mergeAndDeleteStation(Sections sections, Long stationId) {
        Section deletedSection = sections.findByUpStationId(stationId);
        Section updatedSection = sections.findByDownStationId(stationId);
        updatedSection.updateSectionInfoWhenDeleted(deletedSection);
        sectionDao.updateById(updatedSection);
        sectionDao.deleteById(deletedSection.getId());
        return null;
    }

    private Section deleteStartOrEndStation(Sections sections, Long stationId, Line line) {
        Section section = getStartOrEndSection(sections, stationId, line);
        sectionDao.deleteById(section.getId());
        return section;
    }

    private Section getStartOrEndSection(Sections sections, Long stationId, Line line) {
        if (line.isStartStation(stationId)) {
            Section firstSection = sections.getFirstSection();
            return new Section(firstSection.getId(), firstSection.getDownStationId(), line.getEndStationId());
        }
        Section lastSection = sections.getLastSection();
        return new Section(lastSection.getId(), line.getStartStationId(), lastSection.getUpStationId());
    }
}
