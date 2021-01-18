package subway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.SectionDao;
import subway.domain.Section;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SectionService {
    private static final int UNIQUE_MATCH = 1;
    private static final int FIRST_INDEX = 0;
    private static final int MIN_SECTION_SIZE = 1;
    private static final int SECOND_INDEX = 1;

    private SectionDao sectionDao;
    private List<Section> sections;

    @Autowired
    SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    @Transactional
    public void addSectionToLine(Section newSection) {
        sections = sectionDao.getSectionsByLineId(newSection.getLineId());

        checkSameSection(newSection);
        checkNoStation(newSection);

        if (isFirstSection(newSection) || isLastSection(newSection)) {
            sectionDao.save(newSection);
            return;
        }
        addInMiddle(newSection);
    }

    private void checkSameSection(Section newSection) {
        if (sections.stream()
                .anyMatch(section -> section.isSameSection(newSection))) {
            throw new IllegalArgumentException("같은 구역이 이미 등록되어 있습니다.");
        }
    }

    private void checkNoStation(Section newSection) {
        if (sections.stream()
                .noneMatch(section -> section.containStation(newSection))) {
            throw new IllegalArgumentException("노선과 연결할 수 있는 역이 없습니다.");
        }
    }

    private void addInMiddle(Section newSection) {
        Section matchedUpSection = sections.stream()
                .filter(section -> section.getUpStationId().equals(newSection.getUpStationId()))
                .findFirst()
                .orElse(null);

        if (matchedUpSection != null) {
            checkDistance(matchedUpSection.getDistance(), newSection.getDistance());
            sectionDao.save(newSection);
            sectionDao.update(matchedUpSection.getId(), Section.of(
                    matchedUpSection.getId(),
                    matchedUpSection.getLineId(),
                    newSection.getDownStationId(),
                    matchedUpSection.getDownStationId(),
                    matchedUpSection.getDistance() - newSection.getDistance()));
            return;
        }

        Section matchedDownSection = sections.stream()
                .filter(section -> section.getDownStationId().equals(newSection.getDownStationId()))
                .findFirst()
                .orElse(null);

        checkDistance(matchedDownSection.getDistance(), newSection.getDistance());
        sectionDao.save(newSection);
        sectionDao.update(matchedDownSection.getId(), Section.of(
                matchedDownSection.getId(),
                matchedDownSection.getLineId(),
                matchedDownSection.getUpStationId(),
                newSection.getUpStationId(),
                matchedDownSection.getDistance() - newSection.getDistance()));
    }

    private void checkDistance(int originDistance, int newDistance) {
        if (originDistance <= newDistance) {
            throw new IllegalArgumentException("새로 추가할 구간의 거리가 더 큽니다.");
        }
    }

    private boolean isLastSection(Section newSection) {
        boolean uniqueContain = sections.stream()
                .filter(section -> section.getDownStationId().equals(newSection.getUpStationId()))
                .count() == UNIQUE_MATCH;
        boolean notContain = sections.stream()
                .noneMatch(section -> section.getUpStationId().equals(newSection.getUpStationId()));

        return notContain && uniqueContain;
    }

    private boolean isFirstSection(Section newSection) {
        boolean notContain = sections.stream()
                .noneMatch(section -> section.getDownStationId().equals(newSection.getDownStationId()));
        boolean uniqueContain = sections.stream()
                .filter(section -> section.getUpStationId().equals(newSection.getDownStationId()))
                .count() == UNIQUE_MATCH;

        return notContain && uniqueContain;
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        sections = sectionDao.getSectionsByLineId(lineId);
        checkOneSection();

        List<Section> endPointSections = sections.stream()
                .filter(section -> section.getUpStationId().equals(stationId) || section.getDownStationId().equals(stationId))
                .collect(Collectors.toList());

        if (endPointSections.size() == 1) {
            sectionDao.deleteById(endPointSections.get(FIRST_INDEX).getId());
            return;
        }

        mergeSection(stationId, endPointSections);
    }

    private void checkOneSection() {
        if (sections.size() == MIN_SECTION_SIZE) {
            throw new IllegalArgumentException("제거할 수 없습니다.");
        }
    }

    private void mergeSection(Long id, List<Section> sections) {
        int distance = sections.stream()
                .mapToInt(Section::getDistance)
                .sum();

        Long downStationId = sections.get(FIRST_INDEX).getUpStationId().equals(id) ?
                sections.get(FIRST_INDEX).getDownStationId() : sections.get(SECOND_INDEX).getDownStationId();
        Long upStationId = sections.get(SECOND_INDEX).getDownStationId().equals(id) ?
                sections.get(SECOND_INDEX).getUpStationId() : sections.get(FIRST_INDEX).getUpStationId();

        sectionDao.deleteById(sections.get(FIRST_INDEX).getId());
        sectionDao.deleteById(sections.get(SECOND_INDEX).getId());
        sectionDao.save(Section.of(sections.get(FIRST_INDEX).getLineId(), upStationId, downStationId, distance));
    }

    public List<Long> getStationIds(Long lineId) {
        sections = sectionDao.getSectionsByLineId(lineId);

        List<Long> sectionIds = getSectionIds(sections);
        Section firstSection = findFirstSection(sections, sectionIds);

        Map<Long, Section> longToSection = new HashMap<>();
        for (Section section : sections) {
            longToSection.put(section.getUpStationId(), section);
        }

        List<Long> stationIds = new ArrayList<>();
        stationIds.add(firstSection.getUpStationId());
        for (Section iter = firstSection; iter != null; iter = longToSection.get(iter.getDownStationId())) {
            stationIds.add(iter.getDownStationId());
        }

        return stationIds;
    }

    private Section findFirstSection(List<Section> sections, List<Long> sectionIds) {
        return sections.stream()
                .filter(section -> !sectionIds.contains(section.getUpStationId()))
                .findFirst()
                .orElse(null);
    }

    private List<Long> getSectionIds(List<Section> sections) {
        return sections.stream()
                .map(Section::getDownStationId)
                .collect(Collectors.toList());
    }

    public void save(Section section) {
        sectionDao.save(section);
    }
}
