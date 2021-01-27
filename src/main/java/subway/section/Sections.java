package subway.section;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Sections {
    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = Collections.unmodifiableList(sections);
    }

    public static Sections of(List<Section> sections, Long startStationId) {
        return new Sections(getSortedSections(sections, startStationId));
    }

    private static List<Section> getSortedSections(List<Section> sections, Long startStationId) {
        Map<Long, Section> sectionMap = sections.stream()
                .collect(Collectors.toMap(Section::getUpStationId, section -> section));

        List<Section> sortedSections = new ArrayList<>();
        Section curSection = sectionMap.get(startStationId);
        while (curSection != null) {
            sortedSections.add(curSection);
            curSection = sectionMap.getOrDefault(curSection.getDownStationId(), null);
        }
        return sortedSections;
    }

    public List<Long> getStationIds() {
        List<Long> stationIds = new ArrayList<>();
        stationIds.add(sections.get(0).getUpStationId());
        for (Section section : sections) {
            stationIds.add(section.getDownStationId());
        }
        return stationIds;
    }

    public Section findByUpStationId(Long upStationId) {
        return sections.stream()
                .filter(section -> section.getUpStationId().equals(upStationId))
                .findFirst().get();
    }

    public Section findByDownStationId(Long downStationId) {
        return sections.stream()
                .filter(section -> section.getDownStationId().equals(downStationId))
                .findFirst().get();
    }
}
