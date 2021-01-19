package subway.section.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Sections {
    private static final long UNIQUE_MATCH = 1;
    private static final int MIN_SECTION_SIZE = 1;
    private List<Section> sections;

    private Sections(List<Section> sections) {
        this.sections = sections;
    }

    public static Sections of(List<Section> sections) {
        return new Sections(sections);
    }

    public void checkSameSection(Section newSection) {
        if (sections.stream()
                .anyMatch(section -> section.isSameSection(newSection))) {
            throw new RuntimeException("같은 구역이 이미 등록되어 있습니다.");
        }
    }

    public void checkNoStation(Section newSection) {
        if (sections.stream()
                .noneMatch(section -> section.containStation(newSection))) {
            throw new RuntimeException("노선과 연결할 수 있는 역이 없습니다.");
        }
    }

    public boolean isEndPointSection(Section newSection) {
        return isFirstSection(newSection) || isLastSection(newSection);
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

    public Section sameUpSationId(Long id) {
       return sections.stream()
                .filter(section -> section.getUpStationId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public Section sameDownSationId(Long id) {
        return sections.stream()
                .filter(section -> section.getDownStationId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public void checkOneSection() {
        if (sections.size() == MIN_SECTION_SIZE) {
            throw new RuntimeException("제거할 수 없습니다.");
        }
    }

    public List<Section> getEndPointSections(Long stationId) {
        return sections.stream()
                .filter(section -> section.getUpStationId().equals(stationId) || section.getDownStationId().equals(stationId))
                .collect(Collectors.toList());
    }

    public List<Long> getDownStationIds() {
        return sections.stream()
                .map(Section::getDownStationId)
                .collect(Collectors.toList());
    }

    public Section findFirstSection(List<Long> sectionIds) {
        return sections.stream()
                .filter(section -> !sectionIds.contains(section.getUpStationId()))
                .findFirst()
                .orElse(null);
    }

    public List<Long> getStationIds() {
        List<Long> downStationIds = getDownStationIds();
        Section firstSection = findFirstSection(downStationIds);

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
}
