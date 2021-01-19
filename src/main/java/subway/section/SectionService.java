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

    public Section save(Section section) {
        return sectionDao.save(section);
    }

    public Sections getSectionsByLineId(Long lineId) {
        return getOrderedSection(sectionDao.getSectionsByLineId(lineId), lineService.findOne(lineId));
    }

    private Sections getOrderedSection(Sections sections, Line line) {
        Long cur = line.getUpStationId();
        Long dest = line.getDownStationId();
        List<Section> orderedSections = new LinkedList();
        Set<Long> visit = new HashSet<>();

        while (!cur.equals(dest) && visit.add(cur)) {
            Section section = sections.findSectionByUpStationId(cur);
            orderedSections.add(section);
            cur = section.getDownStationId();
        }

        if(visit.contains(cur))
            throw new RuntimeException("반복되는 구간이 존재합니다.");

        return new Sections(orderedSections);
    }

    public boolean deleteSectionById(Long sectionId) {
        return sectionDao.deleteSectionById(sectionId) != 0;
    }

    public boolean saveSection(Section section) {
        Sections sections = sectionDao.getSectionsByLineId(section.getLineId());
        Long existStationId = sections.findStationExistBySection(section);

        if(!checkSectionExist(section, existStationId))
            return false;

        save(section);

        if (section.getUpStationId() == existStationId) {
            addSectionBack(sections, section);
            return true;
        }
        
        addSectionFront(sections, section);
        return true;
    }

    private boolean checkSectionExist(Section section, Long existStationId){
        if (sectionDao.existSection(section) || existStationId == Sections.NOT_EXIST) {
            return false;
        }
        return true;
    }

    private void addSectionBack(Sections sections, Section section) {
        Long existStationId = section.getUpStationId();
        Line line = lineService.findOne(section.getLineId());
        Section nextSection = sections.findSectionByUpStationId(existStationId);
        if (existStationId == line.getDownStationId()) {
            lineService.updateAll(new Line(line.getId(),
                    line.getName(),
                    line.getColor(),
                    line.getUpStationId(),
                    section.getDownStationId()));
            return;
        }
        deleteSectionById(nextSection.getSectionId());
        save(new Section(section.getDownStationId(),
                nextSection.getDownStationId(),
                nextSection.getDistance() - section.getDistance(),
                line.getId()));
    }

    private void addSectionFront(Sections sections, Section section) {
        Long existStationId = section.getDownStationId();
        Line line = lineService.findOne(section.getLineId());
        Section prevSection = sections.findSectionByDownStationId(existStationId);
        if (existStationId == line.getUpStationId()) {
            lineService.updateAll(new Line(line.getId(),
                    line.getName(),
                    line.getColor(),
                    section.getUpStationId(),
                    line.getDownStationId()));
            return;
        }
        deleteSectionById(prevSection.getSectionId());
        save(new Section(prevSection.getUpStationId(),
                section.getUpStationId(),
                prevSection.getDistance() - section.getDistance(),
                line.getId()));
    }

    public boolean deleteSection(Long lineId, Long stationId) {
        Sections sections = getSectionsByLineId(lineId);
        Line line = lineService.findOne(lineId);

        if (!sections.isPossibleToDelete() || sections.findSectionByStationId(stationId) == null) {
            return false;
        }

        Section nextSection = sections.findSectionByUpStationId(stationId);
        Section prevSection = sections.findSectionByDownStationId(stationId);

        if (nextSection != null && prevSection != null) {
            deleteSectionById(nextSection.getSectionId());
            deleteSectionById(prevSection.getSectionId());
            save(new Section(prevSection.getUpStationId(),
                    nextSection.getDownStationId(),
                    prevSection.getDistance() + nextSection.getDistance(),
                    lineId));
        } else if (nextSection != null) {
            deleteSectionById(nextSection.getSectionId());
            lineService.updateAll(new Line(line.getId(),
                    line.getName(),
                    line.getColor(),
                    nextSection.getDownStationId(),
                    line.getDownStationId()));
        } else if (prevSection != null) {
            deleteSectionById(prevSection.getSectionId());
            lineService.updateAll(new Line(line.getId(),
                    line.getName(),
                    line.getColor(),
                    line.getUpStationId(),
                    prevSection.getUpStationId()));
        }
        return true;
    }
}
