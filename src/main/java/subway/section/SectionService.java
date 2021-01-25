package subway.section;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.exceptions.InvalidSectionException;
import subway.line.Line;

import java.util.List;

@Service
public class SectionService {

    public static final String CONTAINS_BOTH_STATIONS_OR_NOT_EXISTS_STATIONS_ERROR_MESSAGE = "두 역이 모두 포함되어 있거나, 두 역 모두 포함되어 있지 않습니다.";
    public static final String SECTIONS_SIZE_ERROR_MESSAGE = "구간이 하나이기 때문에 삭제할 수 없습니다.";
    public static final String SECTION_UPDATE_ERROR_MESSAGE = "구간 정보를 업데이트 하지 못했습니다.";
    public static final String SECTION_SAVE_ERROR_MESSAGE = "구간 저장에 오류가 발생했습니다.";
    public static final String SECTION_DELETE_ERROR_MESSAGE = "구간을 삭제하지 못했습니다.";
    public static final int NO_UPDATED_ROW = 0;

    private SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public void save(Section section) {
        sectionDao.save(section);
    }

    @Transactional
    public void save(Line line, SectionRequest sectionRequest) {
        Sections sections = new Sections(sectionDao.findAllSectionsByLineId(line.getId()), line.getStartStationId());
        if (sections.isContainsBothStationsOrNothing(sectionRequest.getUpStationId(), sectionRequest.getDownStationId())) {
            throw new InvalidSectionException(CONTAINS_BOTH_STATIONS_OR_NOT_EXISTS_STATIONS_ERROR_MESSAGE);
        }
        Section newSection = sectionRequest.toSection(line.getId());
        Section updatedSection = sections.findUpdatedSection(newSection);
        if(sectionDao.updateById(updatedSection) == NO_UPDATED_ROW) {
            throw new InvalidSectionException(SECTION_UPDATE_ERROR_MESSAGE);
        }
        sectionDao.save(newSection).orElseThrow(() -> new InvalidSectionException(SECTION_SAVE_ERROR_MESSAGE));
    }

    public void deleteAllByLineId(Long lineId) {
        sectionDao.deleteAllByLineId(lineId);
    }

    public List<Section> getSectionsByLineId(Long lineId) {
        return sectionDao.findAllSectionsByLineId(lineId);
    }

    @Transactional
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
        if(sectionDao.updateById(updatedSection) == NO_UPDATED_ROW) {
            throw new InvalidSectionException(SECTION_UPDATE_ERROR_MESSAGE);
        }
        if(sectionDao.deleteById(deletedSection.getId()) == NO_UPDATED_ROW) {
            throw new InvalidSectionException(SECTION_DELETE_ERROR_MESSAGE);
        }
        return null;
    }

    private Section deleteStartOrEndStation(Sections sections, Long stationId, Line line) {
        Section section = getStartOrEndSection(sections, stationId, line);
        if(sectionDao.deleteById(section.getId()) == NO_UPDATED_ROW) {
            throw new InvalidSectionException(SECTION_DELETE_ERROR_MESSAGE);
        }
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
