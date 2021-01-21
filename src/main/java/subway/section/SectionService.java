package subway.section;

import org.springframework.stereotype.Service;
import subway.line.Line;
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
        Long existStationId = sections.findStationExistBySection(section);

        if(!sections.checkSectionExist(section))
            return false;

        sectionDao.save(section);

        if (section.getUpStationId() == existStationId) {
            addSectionBack(sections, section);
            return true;
        }

        addSectionFront(sections, section);
        return true;
    }

    private void addSectionBack(Sections sections, Section section) {
        Line line = lineService.findOne(section.getLineId());
        Section nextSection = sections.findNextSection(section);
        if (line.isFinalDownStation(section) || nextSection == null) {
            lineService.updateAll(new Line(line.getId(),
                    line.getName(),
                    line.getColor(),
                    line.getUpStationId(),
                    section.getDownStationId()));
            return;
        }
        sectionDao.deleteSectionById(nextSection.getSectionId());
        sectionDao.save(new Section(section.getDownStationId(),
                nextSection.getDownStationId(),
                nextSection.getDistance() - section.getDistance(),
                line.getId()));
    }

    private void addSectionFront(Sections sections, Section section) {
        Line line = lineService.findOne(section.getLineId());
        Section prevSection = sections.findPrevSection(section);
        if (line.isFinalUpStation(section) || prevSection == null) {
            lineService.updateAll(new Line(line.getId(),
                    line.getName(),
                    line.getColor(),
                    section.getUpStationId(),
                    line.getDownStationId()));
            return;
        }

        sectionDao.deleteSectionById(prevSection.getSectionId());
        sectionDao.save(new Section(prevSection.getUpStationId(),
                section.getUpStationId(),
                prevSection.getDistance() - section.getDistance(),
                line.getId()));
    }

    public boolean deleteSection(Long lineId, Long stationId) {
        Sections sections = getSectionsByLineId(lineId);
        Line line = lineService.findOne(lineId);

        if (!sections.isPossibleToDelete(stationId)) {
            return false;
        }

        Section nextSection = sections.findSectionByUpStationId(stationId);
        Section prevSection = sections.findSectionByDownStationId(stationId);

        if (nextSection != null && prevSection != null) {
            sectionDao.deleteSectionById(nextSection.getSectionId());
            sectionDao.deleteSectionById(prevSection.getSectionId());
            sectionDao.save(new Section(prevSection.getUpStationId(),
                    nextSection.getDownStationId(),
                    prevSection.getDistance() + nextSection.getDistance(),
                    lineId));
        } else if (nextSection != null) {
            sectionDao.deleteSectionById(nextSection.getSectionId());
            lineService.updateAll(new Line(line.getId(),
                    line.getName(),
                    line.getColor(),
                    nextSection.getDownStationId(),
                    line.getDownStationId()));
        } else if (prevSection != null) {
            sectionDao.deleteSectionById(prevSection.getSectionId());
            lineService.updateAll(new Line(line.getId(),
                    line.getName(),
                    line.getColor(),
                    line.getUpStationId(),
                    prevSection.getUpStationId()));
        }
        return true;
    }
}
