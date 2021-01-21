package subway.domain.section;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Sections {
    private static final int UNIQUE_MATCH = 1;
    private static final int MIN_SECTION_SIZE = 1;

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void checkSameSection(Section newSection) {
        if (sections.stream()
                .anyMatch(section -> section.isSameSection(newSection))) {
            throw new IllegalArgumentException("같은 구역이 이미 등록되어 있습니다.");
        }
    }

    public void checkNoStation(Section newSection) {
        if (sections.stream()
                .noneMatch(section -> section.containStation(newSection))) {
            throw new IllegalArgumentException("노선과 연결할 수 있는 역이 없습니다.");
        }
    }

    public void checkOneSection() {
        if (sections.size() == MIN_SECTION_SIZE) {
            throw new IllegalArgumentException("제거할 수 없습니다.");
        }
    }

    public boolean isFirstSection(Section newSection) {
        boolean notContain = sections.stream()
                .noneMatch(section -> section.getDownStationId().equals(newSection.getDownStationId()));
        boolean uniqueContain = sections.stream()
                .filter(section -> section.getUpStationId().equals(newSection.getDownStationId()))
                .count() == UNIQUE_MATCH;

        return notContain && uniqueContain;
    }

    public boolean isLastSection(Section newSection) {
        boolean uniqueContain = sections.stream()
                .filter(section -> section.getDownStationId().equals(newSection.getUpStationId()))
                .count() == UNIQUE_MATCH;
        boolean notContain = sections.stream()
                .noneMatch(section -> section.getUpStationId().equals(newSection.getUpStationId()));

        return notContain && uniqueContain;
    }

    public Section getMatchedUpStation(Section newSection) {
        return sections.stream()
                .filter(section -> section.getUpStationId().equals(newSection.getUpStationId()))
                .findFirst()
                .orElse(null);
    }

    public Section getMatchedDownStation(Section newSection) {
        return sections.stream()
                .filter(section -> section.getDownStationId().equals(newSection.getDownStationId()))
                .findFirst()
                .orElse(null);
    }

    public List<Section> getNeighboringSections(Long stationId) {
        return sections.stream()
                .filter(section -> section.getUpStationId().equals(stationId) || section.getDownStationId().equals(stationId))
                .collect(Collectors.toList());
    }

    private List<Long> getDownStationIds() {
        return sections.stream()
                .map(Section::getDownStationId)
                .collect(Collectors.toList());
    }

    private Section getFirstSection() {
        List<Long> downStationIds = getDownStationIds();
        return sections.stream()
                .filter(section -> !downStationIds.contains(section.getUpStationId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("첫번째 역이 존재하지 않습니다."));
    }

    public List<Long> getSortedStationIds() {
        Section firstSection = getFirstSection();
        Map<Long, Section> idToSection = mapIdToSection();

        List<Long> stationIds = new ArrayList<>();
        stationIds.add(firstSection.getUpStationId());
        for (Section iter = firstSection; iter != null; iter = idToSection.get(iter.getDownStationId())) {
            stationIds.add(iter.getDownStationId());
        }

        return stationIds;
    }

    private Map<Long, Section> mapIdToSection() {
        Map<Long, Section> idToSection = new HashMap<>();
        for (Section section : sections) {
            idToSection.put(section.getUpStationId(), section);
        }
        return idToSection;
    }
}
