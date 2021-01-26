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
        Sections addSections = new Sections(sections.addSection(section), section.getLineId());
        sectionDao.saveSections(addSections);
        lineService.update(sectionDao.getSectionsByLineId(section.getLineId()));
        return true;
    }

    public boolean deleteSection(Long lineId, Long stationId) {
        Sections sections = getSectionsByLineId(lineId);
        Sections delSections = new Sections(sections.deleteStation(stationId), lineId);
        sectionDao.deleteSections(delSections);
        lineService.update(sectionDao.getSectionsByLineId(lineId));
        return true;
    }
}
