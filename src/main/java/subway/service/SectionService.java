package subway.service;

import org.springframework.stereotype.Service;
import subway.domain.Section;
import subway.domain.Sections;
import subway.exception.DeleteSectionException;
import subway.repository.SectionDao;

import java.util.Map;

@Service
public class SectionService {
    private static final String DELETE_EXCEPTION_MESSAGE = "구간이 하나인 노선에서 마지막 구간을 제거할 수 없음";
    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public void create(Section section) {
        sectionDao.save(section);
    }

    public void create(Long lineId, Section section) {
        Sections sections = new Sections(sectionDao.findByLineId(lineId));

        Section lastSection = sections.getLastSection();

        if (section.isNewLastSection(lastSection)) {
            addLastStation(section, lastSection);
            return;
        }

        Section firstSection = sections.getFirstSection();

        if (section.isNewFirstSection(firstSection)) {
            addFirstStation(section, firstSection);
            return;
        }

        addStation(sections, section);
    }

    public void delete(Long lineId, Long stationId) {
        validateDelete(lineId);

        Sections sections = new Sections(sectionDao.findByLineId(lineId));

        Section lastSection = sections.getLastSection();
        if (lastSection.equalsWithDownStation(stationId)) {
            deleteLastStation(sections, lastSection);
            return;
        }

        Section firstSection = sections.getFirstSection();
        if (firstSection.equalsWithUpStation(stationId)) {
            deleteFirstStation(sections, firstSection);
            return;
        }

        deleteStation(sections, stationId);
    }

    public void validateDelete(Long lineId) {
        if (sectionDao.countByLineId(lineId) == 1) {
            throw new DeleteSectionException(DELETE_EXCEPTION_MESSAGE);
        }
    }

    private void addLastStation(Section section, Section lastSection) {
        lastSection.setLastSection(Section.NOT_LAST_SECTION);
        section.setLastSection(Section.LAST_SECTION);
        sectionDao.update(lastSection);
        sectionDao.save(section);
    }

    private void addFirstStation(Section section, Section firstSection) {
        firstSection.setFirstSection(Section.NOT_FIRST_SECTION);
        section.setFirstSection(Section.FIRST_SECTION);
        sectionDao.update(firstSection);
        sectionDao.save(section);
    }

    private void addStation(Sections sections, Section section) {
        Sections separatedSections = sections.getSeparatedSections(section);
        Section newSection = separatedSections.getNewSection();
        Section updateSection = separatedSections.getUpdateSection();
        sectionDao.save(newSection);
        sectionDao.update(updateSection);
    }

    private void deleteLastStation(Sections sections, Section lastSection) {
        Section previousSection = sections.getPreviousSection(lastSection);
        sectionDao.deleteById(lastSection.getId());
        sectionDao.update(previousSection);
    }

    private void deleteFirstStation(Sections sections, Section firstSection) {
        Section nextSection = sections.getNextSection(firstSection);
        sectionDao.deleteById(firstSection.getId());
        sectionDao.update(nextSection);
    }

    private void deleteStation(Sections sections, Long stationId) {
        Sections containedSections = sections.getContainedSections(stationId);
        Section mergeSection = containedSections.getMergeSection(stationId);
        Section deleteSection = containedSections.getDeleteSection();
        sectionDao.deleteById(deleteSection.getId());
        sectionDao.update(mergeSection);
    }
}
