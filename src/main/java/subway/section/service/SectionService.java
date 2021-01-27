package subway.section.service;

import org.springframework.stereotype.Service;
import subway.section.domain.AlignSections;
import subway.section.domain.Section;
import subway.section.repository.SectionDao;

@Service
public class SectionService {
    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public Section save(Section newSection) {
        AlignSections sections = new AlignSections(sectionDao.findByLineId(newSection.getLineId()));
        sections.addSection(newSection);

        sectionDao.deleteByLineId(newSection.getLineId());
        sections.applyToAllSection(sectionDao::save);

        return sections.findByStationId(newSection.getUpStationId(), newSection.getDownStationId());
    }

    public void deleteById(Long id) {
        sectionDao.deleteById(id);
    }

    public void deleteById(Long id, Long stationdId) {
        sectionDao.deleteById(id, stationdId);
    }
}
