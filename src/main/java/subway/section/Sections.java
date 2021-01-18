package subway.section;

import subway.station.Station;

import java.util.List;

public class Sections {
    protected List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public int getSize() {
        return sections.size();
    }

    protected Section findSameUpStationSection(Section section) {
        return sections.stream()
                .filter(it -> it.hasSameUpStation(section))
                .findFirst()
                .orElse(null);
    }

    protected Section findSameDownStationSection(Section section) {
        return sections.stream()
                .filter(it -> it.hasSameDownStation(section))
                .findFirst()
                .orElse(null);
    }

    public boolean containsStation(Station station) {
        return findSectionByStation(station) != null;
    }

    protected Section findSectionByStation(Station station) {
        return sections.stream()
                .filter(section -> section.hasStation(station))
                .findFirst()
                .orElse(null);
    }

}
