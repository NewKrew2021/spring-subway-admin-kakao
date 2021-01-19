package subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Sections;

import java.util.LinkedList;
import java.util.List;

@Service
public class SectionServiceImpl implements SectionService {

    private final SectionDao sectionDao;
    private final LineDao lineDao;

    public SectionServiceImpl(SectionDao sectionDao, LineDao lineDao) {
        this.sectionDao = sectionDao;
        this.lineDao = lineDao;
    }

    public Section save(Section section) {
        return sectionDao.save(section);
    }

    public Sections getSectionsByLineId(Long lineId) {
        return getOrderedSection(sectionDao.getSectionsByLineId(lineId), lineDao.findOne(lineId));
    }

    private Sections getOrderedSection(Sections sections, Line line) {
        Long cur = line.getUpStationId();
        Long dest = line.getDownStationId();
        List<Section> orderedSections = new LinkedList();

        while (!cur.equals(dest)) {
            Section section = sections.findSectionByUpStationId(cur);
            orderedSections.add(section);
            cur = section.getDownStationId();
        }
        return new Sections(orderedSections);
    }

    @Transactional
    public boolean saveSection(Section section) {
        if (sectionDao.existSection(section)) {
            return false;
        }
        return checkAndAddSection(section);
    }

    private boolean checkAndAddSection(Section section) {
        Sections sections = sectionDao.getSectionsByLineId(section.getLineId());
        Long existStationId = sections.findStationExist(section);
        if (existStationId == -1) {
            return false;
        }
        save(section);
        if (section.getUpStationId() == existStationId) {
            addSectionBack(sections, section);
            return true;
        }
        addSectionFront(sections, section);
        return true;
    }

    private void addSectionBack(Sections sections, Section section) {
        Long existStationId = section.getUpStationId();
        Line line = lineDao.findOne(section.getLineId());
        Section nextSection = sections.findSectionByUpStationId(existStationId);
        if (existStationId == line.getDownStationId()) {
            lineDao.updateAll(new Line(line.getId(), line.getName(), line.getColor(), line.getUpStationId(), section.getDownStationId()));
            return;
        }
        deleteSectionById(nextSection.getSectionId());
        save(new Section(section.getDownStationId(), nextSection.getDownStationId(), nextSection.getDistance() - section.getDistance(), line.getId()));
    }

    private void addSectionFront(Sections sections, Section section) {
        Long existStationId = section.getDownStationId();
        Line line = lineDao.findOne(section.getLineId());
        Section prevSection = sections.findSectionByDownStationId(existStationId);
        if (existStationId == line.getUpStationId()) {
            lineDao.updateAll(new Line(line.getId(), line.getName(), line.getColor(), section.getUpStationId(), line.getDownStationId()));
            return;
        }
        deleteSectionById(prevSection.getSectionId());
        save(new Section(prevSection.getUpStationId(), section.getUpStationId(), prevSection.getDistance() - section.getDistance(), line.getId()));
    }

    @Transactional
    public boolean deleteSection(Long lineId, Long stationId) {
        Sections sections = getSectionsByLineId(lineId);
        Line line = lineDao.findOne(lineId);

        if (!sections.isPossibleToDelete() || sections.findSectionByStationId(stationId) == null) {
            return false;
        }

        Section nextSection = sections.findSectionByUpStationId(stationId);
        Section prevSection = sections.findSectionByDownStationId(stationId);

        if (nextSection != null && prevSection != null) {
            deleteSectionById(nextSection.getSectionId());
            deleteSectionById(prevSection.getSectionId());
            save(new Section(prevSection.getUpStationId(), nextSection.getDownStationId(), prevSection.getDistance() + nextSection.getDistance(), lineId));
        } else if (nextSection != null) {
            deleteSectionById(nextSection.getSectionId());
            lineDao.updateAll(new Line(line.getId(), line.getName(), line.getColor(), nextSection.getDownStationId(), line.getDownStationId()));
        } else if (prevSection != null) {
            deleteSectionById(prevSection.getSectionId());
            lineDao.updateAll(new Line(line.getId(), line.getName(), line.getColor(), line.getUpStationId(), prevSection.getUpStationId()));
        }
        return true;
    }

    public boolean deleteSectionById(Long sectionId) {
        return sectionDao.deleteSectionById(sectionId) != 0;
    }

    public void deleteSectionByLineId(Long lineId) {
        sectionDao.deleteSectionByLineId(lineId);
    }
}
