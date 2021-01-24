package subway.section;

import org.springframework.stereotype.Service;
import subway.line.Line;
import subway.line.LineInfoChangedResult;
import subway.line.LineService;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Service
public class SectionService{

    private final SectionDao sectionDao;
    private final LineService lineService;

    public SectionService(SectionDao sectionDao, LineService lineService) {
        this.sectionDao = sectionDao;
        this.lineService = lineService;
    }

    public Sections getSectionsByLineId(Long lineId) {
        return sectionDao.getSectionsByLineId(lineId).getOrderedSection(lineService.findOne(lineId));
    }

    public boolean saveSection(Section section) {
        Sections sections = sectionDao.getSectionsByLineId(section.getLineId());
        sections.initAddSections();
        sections.initDelSections();
        LineInfoChangedResult result = sections.addSection(lineService.findOne(section.getLineId()), section);
        lineService.update(result);
        sectionDao.saveSections(new Sections(sections.getAddSections()));
        sectionDao.deleteSections(new Sections(sections.getDelSections()));
        return true;
    }

    public boolean deleteSection(Long lineId, Long stationId) {
        Sections sections = getSectionsByLineId(lineId);
        sections.initAddSections();
        sections.initDelSections();
        LineInfoChangedResult result = sections.deleteStation(lineId, stationId);
        lineService.update(result);
        sectionDao.saveSections(new Sections(sections.getAddSections()));
        sectionDao.deleteSections(new Sections(sections.getDelSections()));
        return true;
    }
}
