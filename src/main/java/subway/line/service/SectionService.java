package subway.line.service;

import org.springframework.stereotype.Service;
import subway.line.dao.SectionDao;
import subway.line.domain.Section;
import subway.line.domain.Sections;

import java.util.List;

@Service
public class SectionService {
    private SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public void insert(Section section) {
        Sections sections = new Sections(sectionDao.showAllByLineId(section.getLineId()));
        sections.addSection(section);
        sectionDao.deleteByLineId(section.getLineId());
        sectionDao.saveAll(sections.getSections());
    }

    public void delete(Long lineId, Long stationId) {
        Sections sections = new Sections(sectionDao.findByLineId(lineId));
        sections.deleteSection(lineId, stationId);
        sectionDao.deleteByLineId(lineId);
        sectionDao.saveAll(sections.getSections());
    }
}
