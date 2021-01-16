package subway.domain;

import java.util.*;
import java.util.stream.Collectors;

public class OrderedSections {
    private final List<Section> orderedSections = new ArrayList<>();

    public OrderedSections(List<Section> sections) {
        Long upStation = findFirstStationFromSections(sections);
        Map<Long, Section> connection = generateConnection(sections);

        Long currentStation = upStation;
        for (int i = 0; i < sections.size(); ++i) {
            Section currentSection = connection.get(currentStation);
            orderedSections.add(currentSection);
            currentStation = currentSection.getDownStationId();
        }
    }

    public List<Long> getOrderedStationIds() {
        List<Long> stationIds = orderedSections.stream()
                .map(Section::getUpStationId)
                .collect(Collectors.toList());
        stationIds.add(orderedSections.get(orderedSections.size() - 1).getDownStationId());
        return stationIds;
    }

    public Section findSectionToSplit(Section sectionToAdd) {
        Optional<Section> optionalSection = orderedSections.stream()
                .filter(section -> section.getUpStationId().equals(sectionToAdd.getUpStationId()))
                .findFirst();

        return optionalSection.orElseGet(() -> orderedSections.stream()
                .filter(section -> section.getDownStationId().equals(sectionToAdd.getDownStationId()))
                .findFirst().orElseThrow(IllegalArgumentException::new));
    }

    public boolean isAddToEdgeCase(Section section) {
        return getFirstStation().equals(section.getDownStationId()) ||
                getLastStation().equals(section.getUpStationId());
    }

    private Long getFirstStation() {
        return orderedSections.get(0).getUpStationId();
    }

    private Long getLastStation() {
        return orderedSections.get(orderedSections.size() - 1).getDownStationId();
    }

    private static Long findFirstStationFromSections(List<Section> sections) {
        List<Long> upStations = sections.stream()
                .map(Section::getUpStationId)
                .collect(Collectors.toList());
        List<Long> downStations = sections.stream()
                .map(Section::getDownStationId)
                .collect(Collectors.toList());
        return upStations.stream().filter(station -> !downStations.contains(station))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    private static Map<Long, Section> generateConnection(List<Section> sections) {
        Map<Long, Section> connection = new HashMap<>();
        sections.forEach(section -> connection.put(section.getUpStationId(), section));
        return connection;
    }
}
