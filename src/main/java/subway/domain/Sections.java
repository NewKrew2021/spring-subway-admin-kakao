package subway.domain;

import java.util.List;

public class Sections {

    private final List<Section> sections;

    public Sections (List<Section> sections) {
        this.sections = sections;
    }

    public Section sameUpStationOrDownStation(Section other) {
        return sections.stream()
                .filter(s -> s.getPointType().equals(Line.USE) &&
                        s.getUpStation().equals(other.getUpStation()) || s.getDownStation().equals(other.getDownStation()))
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
                .filter(section -> !section.isHeadType()
                        && section.getUpStation().equals(currentSection.getDownStation()))
                .findAny()
                .get();
    }

    public Section findHeadSection() {
        return sections.stream()
                .filter(section -> section.isHeadType())
                .findAny()
                .get();
    }
}
