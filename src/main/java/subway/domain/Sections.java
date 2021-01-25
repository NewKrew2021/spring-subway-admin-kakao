package subway.domain;

import java.util.List;
import java.util.stream.Collectors;

public class Sections {

    private static final int MINIMUM_SECTION_COUNT = 1;
    private final List<Section> sections;

    public Sections (List<Section> sections) {
        this.sections = sections;
    }

    public Section sameUpStationOrDownStation(Section other) {
        return sections.stream()
                .filter(s -> s.getUpStation().equals(other.getUpStation()) || s.getDownStation().equals(other.getDownStation()))
                .findAny()
                .get();
    }

    public boolean isNotExistStations(Section section) {
        return sections.stream()
                .noneMatch(s -> s.existStation(section));
    }

    public boolean hasSameSection(Section section) {
        return sections.stream()
                .anyMatch(s -> s.equals(section));
    }

    public Section findNextSection(Section currentSection) {
        return sections.stream()
                .filter(section -> section.getUpStation().equals(currentSection.getDownStation()))
                .findAny()
                .orElse(null);
    }

    public Section findHeadSection() {
        List<Station> downStations = sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());

        return sections.stream()
                .filter(section -> !downStations.contains(section.getUpStation()))
                .findAny()
                .get();
    }

    public Section findTailSection() {
        List<Station> upStations = sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());

        return sections.stream()
                .filter(section -> !upStations.contains(section.getDownStation()))
                .findAny()
                .get();
    }

    public boolean canNotDelete() {
        return sections.size() <= MINIMUM_SECTION_COUNT;
    }

    public boolean isTerminalStation(Long stationId) {
        return findHeadSection().getUpStation().getId().equals(stationId)
                || findTailSection().getDownStation().getId().equals(stationId);
    }

    public Section findFrontSection(Long stationId) {
        return sections.stream()
                .filter(section -> section.getDownStation().getId().equals(stationId))
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
    }

    public Section findRearSection(Long stationId) {
        return sections.stream()
                .filter(section -> section.getUpStation().getId().equals(stationId))
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
    }

    public boolean isExtendTerminal(Section newSection) {
        Section head = findHeadSection();
        Section tail = findTailSection();
        if(newSection.getDownStation().equals(head.getUpStation())){
            return true;
        }
        if (newSection.getUpStation().equals(tail.getDownStation())){
            return true;
        }

        return false;
    }
}
