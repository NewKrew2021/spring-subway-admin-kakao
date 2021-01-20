package subway.domain;

import java.util.*;
import java.util.stream.Collectors;

public class SectionsInLine extends Sections {
    public SectionsInLine(List<Section> sections) {
        super(sections);
        if (!areInSameLine(sections)) throw new RuntimeException();
    }

    public List<Station> findSortedStations() {
        if (sections.size() == 0) return new ArrayList<>();

        Map<Station, Station> upStationToDownStation = new HashMap<>();
        for (Section section : sections) {
            upStationToDownStation.put(section.getUpStation(), section.getDownStation());
        }

        List<Station> stations = new ArrayList<>();
        Station currentStation = findUpTerminalSection().getUpStation();

        while (currentStation != null) {
            stations.add(currentStation);
            currentStation = upStationToDownStation.get(currentStation);
        }

        return stations;
    }

    public Section findContainingExistingSection(Section section) {
        if (findSameUpStationSection(section) != null) {
            return findSameUpStationSection(section);
        }
        if (findSameDownStationSection(section) != null) {
            return findSameDownStationSection(section);
        }
        return null;
    }

    private boolean areInSameLine(List<Section> sections) {
        return sections.stream()
                .map(section -> section.getLine())
                .collect(Collectors.toSet())
                .size() <= 1;
    }

    public Section findUpTerminalSection() {
        Map<Station, Long> downStationMap = sections.stream()
                .collect(Collectors.groupingBy(Section::getDownStation, Collectors.counting()));
        return sections.stream()
                .filter(section -> downStationMap.get(section.getUpStation()) == null)
                .findFirst()
                .orElse(null);
    }

    public Section findDownTerminalSection() {
        Map<Station, Long> upStationMap = sections.stream()
                .collect(Collectors.groupingBy(Section::getUpStation, Collectors.counting()));
        return sections.stream()
                .filter(section -> upStationMap.get(section.getDownStation()) == null)
                .findFirst()
                .orElse(null);
    }

    public boolean ofTerminalStationIs(Station station) {
        return isOfUpTerminalStation(station) || isOfDownTerminalStation(station);
    }

    public boolean isOfUpTerminalStation(Station station) {
        return findUpTerminalSection().getUpStation().equals(station);
    }

    public boolean isOfDownTerminalStation(Station station) {
        return findDownTerminalSection().getDownStation().equals(station);
    }

    public Section findUpwardSectionByStation(Station station) {
        return sections.stream()
                .filter(section -> section.getDownStation().equals(station))
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    public Section findDownWardSectionByStation(Station station) {
        return sections.stream()
                .filter(section -> section.getUpStation().equals(station))
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    public void mapStation(StationMapper stationMapper) {
        sections.forEach(section -> {
                    section.setUpStation(stationMapper.mapById(section.getUpStation().getId()));
                    section.setDownStation(stationMapper.mapById(section.getDownStation().getId()));
                });
    }

    public interface StationMapper {
        Station mapById(Long stationId);
    }
}
