package subway.domain;

import subway.exception.custom.CannotAddSectionException;
import subway.exception.custom.IllegalSectionException;

import java.util.*;
import java.util.stream.Collectors;

public class OrderedSections extends Sections {
    private final List<Section> orderedSections = new ArrayList<>();

    public OrderedSections(List<Section> sections) {
        super(sections);
        Map<Long, Section> connection = super.generateConnection();

        Section currentSection = connection.get(super.findFirstStation());
        for (int i = 0; i < sections.size(); ++i) {
            orderedSections.add(currentSection);
            currentSection = connection.get(currentSection.getDownStationId());
        }
        validateOrderedSections(sections);
    }

    private void validateOrderedSections(List<Section> sections) {
        if (sections.size() != orderedSections.size()) {
            throw new IllegalSectionException();
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
        return findFirstStation().equals(sectionToAdd.getDownStationId()) ||
                findLastStation().equals(sectionToAdd.getUpStationId());
    }

    public void validateSectionAddRequest(Section sectionToAdd) {
        if (countContainedStation(sectionToAdd) != 1) {
            throw new CannotAddSectionException();
        }
    }

    public int countContainedStation(Section section) {
        return (int) getOrderedStationIds().stream()
                .filter(stationId -> stationId.equals(section.getDownStationId()) ||
                        stationId.equals(section.getUpStationId()))
                .count();
    }

    @Override
    public Long findFirstStation() {
        return orderedSections.get(0).getUpStationId();
    }

    public Long findLastStation() {
        return orderedSections.get(orderedSections.size() - 1).getDownStationId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        OrderedSections that = (OrderedSections) o;
        return Objects.equals(orderedSections, that.orderedSections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), orderedSections);
    }
}
