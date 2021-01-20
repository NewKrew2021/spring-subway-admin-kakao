package subway.section.domain;

import java.util.List;
import java.util.stream.Collectors;

public class Sections {

    private List<Section> sections;

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
                .filter(s -> s.existStation(section))
                .collect(Collectors.toList())
                .size() == 0;
    }

    public boolean hasSameSection(Section section) {
        return sections.stream()
                .filter(s -> s.equals(section))
                .collect(Collectors.toList())
                .size() != 0;
    }

    public Section findNextSection(Section currentSection) {
        return sections.stream()
                .filter(section -> section.getUpStation().equals(currentSection.getDownStation()))
                .findAny()
                .get();
    }
}
