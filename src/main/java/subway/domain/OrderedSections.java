package subway.domain;

import subway.exception.custom.CannotAddSectionException;
import subway.exception.custom.SameUpstationDownStationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class OrderedSections {
    private final List<Section> orderedSections = new ArrayList<>();

    public OrderedSections(Sections sections) {
        Long upStation = sections.findFirstStation();
        Map<Long, Section> connection = sections.generateConnection();

        Section currentSection = connection.get(upStation);
        for (int i = 0; i < sections.size(); ++i) {
            orderedSections.add(currentSection);
            currentSection = connection.get(currentSection.getDownStationId());
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
                .findFirst().orElseThrow(CannotAddSectionException::new));
    }

    public boolean isAddToEdgeCase(Section sectionToAdd) {
        return getFirstStation().equals(sectionToAdd.getDownStationId()) ||
                getLastStation().equals(sectionToAdd.getUpStationId());
    }

    public void validateSectionAddRequest(Section sectionToAdd) {
        if (sectionToAdd.getUpStationId().equals(sectionToAdd.getDownStationId())) {
            throw new SameUpstationDownStationException();
        }

        if (countContainedStation(sectionToAdd) != 1) {
            throw new CannotAddSectionException();
        }
    }

    private int countContainedStation(Section section) {
        return (int) getOrderedStationIds().stream()
                .filter(stationId -> stationId.equals(section.getDownStationId()) ||
                        stationId.equals(section.getUpStationId()))
                .count();
    }

    private Long getFirstStation() {
        return orderedSections.get(0).getUpStationId();
    }

    private Long getLastStation() {
        return orderedSections.get(orderedSections.size() - 1).getDownStationId();
    }

}
