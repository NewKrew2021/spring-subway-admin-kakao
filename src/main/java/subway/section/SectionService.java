package subway.section;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SectionService {
    private static final int FIRST_INDEX = 0;
    private static final int SECOND_INDEX = 1;

    private final SectionDao sectionDao;

    SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    @Transactional
    public void addSectionToLine(Section newSection) {
        Sections sections = new Sections(sectionDao.getSectionsByLineId(newSection.getLineId()));

        sections.checkSameSection(newSection);
        sections.checkNoStation(newSection);

        if (sections.isFirstSection(newSection) || sections.isLastSection(newSection)) {
            sectionDao.save(newSection);
            return;
        }

        addInMiddle(sections, newSection);
    }

    private void addInMiddle(Sections sections, Section newSection) {
        Section matchedUpSection = sections.getMatchedUpStation(newSection);

        if (matchedUpSection != null) {
            checkDistance(matchedUpSection, newSection);
            addStationWhenMatchedUp(newSection, matchedUpSection);
            return;
        }

        Section matchedDownSection = sections.getMatchedDownStation(newSection);

        checkDistance(matchedDownSection, newSection);
        addStationWhenMatchedDown(newSection, matchedDownSection);
    }

    private void addStationWhenMatchedDown(Section newSection, Section matchedDownSection) {
        sectionDao.save(newSection);
        sectionDao.update(matchedDownSection.getId(), Section.of(
                matchedDownSection.getId(),
                matchedDownSection.getLineId(),
                matchedDownSection.getUpStationId(),
                newSection.getUpStationId(),
                matchedDownSection.getDistance() - newSection.getDistance()));
    }

    private void addStationWhenMatchedUp(Section newSection, Section matchedUpSection) {
        sectionDao.save(newSection);
        sectionDao.update(matchedUpSection.getId(), Section.of(
                matchedUpSection.getId(),
                matchedUpSection.getLineId(),
                newSection.getDownStationId(),
                matchedUpSection.getDownStationId(),
                matchedUpSection.getDistance() - newSection.getDistance()));
    }

    private void checkDistance(Section section, Section newSection) {
        if (section.getDistance() <= newSection.getDistance()) {
            throw new IllegalArgumentException("새로 추가할 구간의 거리가 더 큽니다.");
        }
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Sections sections = new Sections(sectionDao.getSectionsByLineId(lineId));
        sections.checkOneSection();

        List<Section> neighboringSections = sections.getNeighboringSections(stationId);
        if (neighboringSections.size() == 1) {
            sectionDao.deleteById(neighboringSections.get(FIRST_INDEX).getId());
            return;
        }

        Section section1 = neighboringSections.get(FIRST_INDEX);
        Section section2 = neighboringSections.get(SECOND_INDEX);
        mergeSection(section1, section2);
    }

    private void mergeSection(Section section1, Section section2) {
        int distance = section1.getDistance() + section2.getDistance();
        if (section1.getUpStationId().equals(section2.getDownStationId())) {
            Section temp = section1;
            section1 = section2;
            section2 = temp;
        }

        Long upStationId = section1.getUpStationId();
        Long downStationId = section2.getDownStationId();

        sectionDao.deleteById(section1.getId());
        sectionDao.deleteById(section2.getId());
        sectionDao.save(Section.of(section1.getLineId(), upStationId, downStationId, distance));
    }

    public List<Long> getStationIds(Long lineId) {
        Sections sections = new Sections(sectionDao.getSectionsByLineId(lineId));
        return sections.getSortedStationIds();
    }

    public void save(Section section) {
        sectionDao.save(section);
    }

    public void deleteLineId(Long lineId) {
        sectionDao.deleteByLineId(lineId);
    }
}
