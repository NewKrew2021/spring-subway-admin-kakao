package subway.section;

import org.springframework.stereotype.Service;
import subway.line.Line;
import subway.line.LineService;

import java.util.LinkedList;
import java.util.List;

@Service
public class SectionServiceImpl implements SectionService {

    private final SectionDao sectionDao;
    private final LineService lineService;

    public SectionServiceImpl(SectionDao sectionDao, LineService lineService) {
        this.sectionDao = sectionDao;
        this.lineService = lineService;
    }

    public Section save(Section section) {
        return sectionDao.save(section);
    }

    public Sections getSectionsByLineId(Long lineId) {
        Sections sections = sectionDao.getSectionsByLineId(lineId);
        Line line = lineService.findOne(lineId);
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

    public boolean deleteSectionById(Long sectionId) {
        return sectionDao.deleteSectionById(sectionId) != 0;
    }

    public boolean saveSection(Long lineId, Section section) {
        Sections sections = getSectionsByLineId(lineId);
        Line line = lineService.findOne(lineId);
        Long upId = section.getUpStationId();
        Long downId = section.getDownStationId();

        Long existStationId = sections.findStationExist(section);

        Section nextSection = sections.findSectionByUpStationId(existStationId);
        Section prevSection = sections.findSectionByDownStationId(existStationId);

        if (sections.existSection(section)) {
            return false;
        }

        if (existStationId == -1) {
            return false;
        }

        if (section.getUpStationId() == existStationId) {
            save(section);
            if (existStationId == line.getDownStationId()) {
                lineService.updateAll(new Line(line.getId(), line.getName(), line.getColor(), line.getUpStationId(), downId));
                return true;
            }
            int distance = nextSection.getDistance() - section.getDistance();
            if (distance <= 0) {
                return false;
            }
            deleteSectionById(nextSection.getSectionId());
            save(new Section(downId, nextSection.getDownStationId(), distance, lineId));
        }

        if (section.getDownStationId() == existStationId) {
            save(section);
            if (existStationId == line.getUpStationId()) {
                lineService.updateAll(new Line(line.getId(), line.getName(), line.getColor(), upId, line.getDownStationId()));
                return true;
            }
            int distance = prevSection.getDistance() - section.getDistance();
            if (distance <= 0) {
                return false;
            }
            deleteSectionById(prevSection.getSectionId());
            save(new Section(prevSection.getUpStationId(), upId, distance, lineId));
        }
        return true;
    }

    public boolean deleteSection(Long lineId, Long stationId) {
        Sections sections = getSectionsByLineId(lineId);
        Line line = lineService.findOne(lineId);

        if (!sections.isPossibleToDelete()) {
            return false;
        }
        Section nextSection = sections.findSectionByUpStationId(stationId);
        Section prevSection = sections.findSectionByDownStationId(stationId);

        if (nextSection == null && prevSection == null) {
            return false;
        } else if (nextSection != null && prevSection != null) {
            deleteSectionById(nextSection.getSectionId());
            deleteSectionById(prevSection.getSectionId());

            save(new Section(prevSection.getUpStationId(), nextSection.getDownStationId(), prevSection.getDistance() + nextSection.getDistance(), lineId));
        } else if (nextSection != null) {
            deleteSectionById(nextSection.getSectionId());
            lineService.updateAll(new Line(line.getId(), line.getName(), line.getColor(), nextSection.getDownStationId(), line.getDownStationId()));
        } else if (prevSection != null) {
            deleteSectionById(prevSection.getSectionId());
            lineService.updateAll(new Line(line.getId(), line.getName(), line.getColor(), line.getUpStationId(), prevSection.getUpStationId()));
        }
        return true;
    }
}
