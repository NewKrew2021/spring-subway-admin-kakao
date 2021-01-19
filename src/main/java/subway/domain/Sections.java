package subway.domain;

import subway.exception.custom.IllegalSectionException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Sections {
    private final List<Section> sections;

    public Sections(List<Section> Sections) {
        this.sections = Sections;
    }

    public Section getUpMatchSection(Long stationId) {
        return sections.stream()
                .filter(section -> section.getUpStationId().equals(stationId))
                .findFirst()
                .orElse(null);
    }

    public Section getDownMatchSection(Long stationId) {
        return sections.stream()
                .filter(section -> section.getDownStationId().equals(stationId))
                .findFirst()
                .orElse(null);
    }

    public Long findFirstStation() {
        List<Long> upStations = sections.stream()
                .map(Section::getUpStationId)
                .collect(Collectors.toList());
        List<Long> downStations = sections.stream()
                .map(Section::getDownStationId)
                .collect(Collectors.toList());
        return upStations.stream().filter(station -> !downStations.contains(station))
                .findFirst()
                .orElseThrow(IllegalSectionException::new);
    }

    public Map<Long, Section> generateConnection() {
        Map<Long, Section> connection = new HashMap<>();
        sections.forEach(section -> connection.put(section.getUpStationId(), section));
        return connection;
    }

    public int size() {
        return sections.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sections sections1 = (Sections) o;
        return Objects.equals(sections, sections1.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sections);
    }
}
