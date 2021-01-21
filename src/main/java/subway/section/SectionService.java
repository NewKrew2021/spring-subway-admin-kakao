package subway.section;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.section.exceptions.InvalidAddSectionException;
import subway.section.exceptions.InvalidDeleteSectionException;
import subway.section.exceptions.NoSuchSectionException;

import java.util.List;

@Service
public class SectionService {
    private static final int FIRST_INDEX = 0;
    private static final int MIN_SECTION_SIZE = 1;
    private static final int SECOND_INDEX = 1;

    private final SectionDao sectionDao;

    @Autowired
    SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    @Transactional
    public void addSectionToLine(Section newSection) {
        Sections sections = new Sections(sectionDao.findSectionsByLineId(newSection.getLineId()));
        sections.validateAddSection(newSection);

        if (sections.isFirstSection(newSection) || sections.isLastSection(newSection)) {
            sectionDao.save(newSection);
            return;
        }

        addInMiddle(sections, newSection);
    }

    private void addInMiddle(Sections sections, Section newSection) {
        Section matchedUpSection = sections.getMatchedUpStation(newSection);

        if (matchedUpSection != null) {
            checkDistance(newSection, matchedUpSection);
            sectionDao.save(newSection);
            sectionDao.update(matchedUpSection.getId(), Section.of(
                    matchedUpSection.getId(),
                    matchedUpSection.getLineId(),
                    newSection.getDownStationId(),
                    matchedUpSection.getDownStationId(),
                    matchedUpSection.getDistance() - newSection.getDistance()));
            return;
        }

        Section matchedDownSection = sections.getMatchedDownStation(newSection);

        if (matchedDownSection == null) {
            throw new InvalidAddSectionException("section을 해당 line에 추가할 수 없습니다.");
        }

        checkDistance(newSection, matchedDownSection);
        sectionDao.save(newSection);
        sectionDao.update(matchedDownSection.getId(), Section.of(
                matchedDownSection.getId(),
                matchedDownSection.getLineId(),
                matchedDownSection.getUpStationId(),
                newSection.getUpStationId(),
                matchedDownSection.getDistance() - newSection.getDistance()));
    }

    private void checkDistance(Section newSection, Section matchedUpSection) {
        if (matchedUpSection.isShorterThan(newSection)) {
            throw new InvalidAddSectionException("추가하려는 구간이 기존의 구간보다 같거나 더 깁니다.");
        }
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Sections sections = new Sections(sectionDao.findSectionsByLineId(lineId));
        checkSectionsSize(sections);

        List<Section> sectionsContainStationId = sections.getSectionsContainStationId(stationId);

        if (sectionsContainStationId.size() == 1) {
            sectionDao.deleteById(sectionsContainStationId.get(FIRST_INDEX).getId());
            return;
        }

        deleteAndMergeSection(stationId, sectionsContainStationId);
    }

    private void checkSectionsSize(Sections sections) {
        if (sections.size() == MIN_SECTION_SIZE) {
            throw new InvalidDeleteSectionException("구간이 하나 남았을 때는 더 이상 제거할 수 없습니다.");
        }
    }

    private void deleteAndMergeSection(Long stationId, List<Section> sections) {
        Section sectionA = sections.get(FIRST_INDEX);
        Section sectionB = sections.get(SECOND_INDEX);

        Long upStationId = sectionB.equalsWithDownStationId(stationId) ?
                sectionB.getUpStationId() : sectionA.getUpStationId();

        Long downStationId = sectionA.equalsWithUpStationId(stationId) ?
                sectionA.getDownStationId() : sectionB.getDownStationId();

        int distance = sections.stream()
                .mapToInt(Section::getDistance)
                .sum();

        sectionDao.deleteById(sectionA.getId());
        sectionDao.deleteById(sectionB.getId());

        sectionDao.save(Section.of(sectionA.getLineId(), upStationId, downStationId, distance));
    }

    public List<Long> getStationIds(Long lineId) {
        Sections sections = new Sections(sectionDao.findSectionsByLineId(lineId));

        return sections.getStationIds();
    }

    public void save(Section section) {
        sectionDao.save(section);
    }

    public void delete(Long id) {
        try {
            checkExistSection(id);
            sectionDao.deleteById(id);
        } catch (Exception e) {
            System.out.println(e.getClass());
            throw new InvalidDeleteSectionException("삭제하려는 section이 존재하지 않습니다.");
        }
    }

    private void checkExistSection(Long id) {
        if (sectionDao.findById(id) == null) {
            throw new NoSuchSectionException(id);
        }
    }
}
