package subway.domain.section;

import subway.domain.station.Station;
import subway.domain.station.Stations;
import subway.exception.section.IllegalSectionsException;
import subway.exception.section.InvalidStationException;
import subway.exception.section.SectionDeletionException;
import subway.exception.section.SectionSplitException;

import java.util.*;
import java.util.stream.Collectors;

public class Sections {
    private List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
        sortByOrder();
    }

    public Long findFirstStation() {
        List<Long> upStations = sections.stream().map(Section::getUpStationId).collect(Collectors.toList());
        List<Long> downStations = sections.stream().map(Section::getDownStationId).collect(Collectors.toList());
        return upStations.stream().filter(station -> !downStations.contains(station)).findFirst().orElseThrow(IllegalSectionsException::new);
    }

    public Stations getAllStations() {
        List<Station> stations = sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
        Station lastStation = sections.get(sections.size() - 1).getDownStation();
        stations.add(lastStation);
        return new Stations(stations);
    }

    public void validateSectionSplit(Section section) {
        Stations stations = getAllStations();
        if (stations.contain(section.getUpStationId()) == stations.contain(section.getDownStationId())) {
            throw new SectionSplitException();
        }
    }

    public boolean checkSplit(Section section) {
        return !(sections.get(0).getUpStationId().equals(section.getDownStationId()) ||
                sections.get(sections.size() - 1).getDownStationId().equals(section.getUpStationId()));
    }

    public Section findSectionToSplit(Section newSection) {
        Section sectionToSplit = getSectionFromUpStationId(newSection.getUpStationId());
        if(sectionToSplit == null) {
            return getSectionFromDownStationId(newSection.getDownStationId());
        }
        return sectionToSplit;
    }

    public boolean contain(Long stationId) {
        return getAllStations().contain(stationId);
    }

    public Section getSectionFromUpStationId(Long stationId) {
        return sections.stream()
                .filter(section -> section.getUpStationId().equals(stationId))
                .findFirst()
                .orElse(null);
    }

    public Section getSectionFromDownStationId(Long stationId) {
        return sections.stream()
                .filter(section -> section.getDownStationId().equals(stationId))
                .findFirst()
                .orElse(null);
    }

    public void validateDeleteSection(Long stationId) {
        if(!contain(stationId)) {
            throw new InvalidStationException(stationId);
        }
        if(sections.size() == 1) {
            throw new SectionDeletionException();
        }
    }

    private void sortByOrder() {
        Map<Long, Section> connection = generateConnection();
        Long currentStation = findFirstStation();
        List<Section> orderedSections = new ArrayList<>();
        for (int i = 0; i < sections.size(); ++i) {
            Section currentSection = connection.get(currentStation);
            orderedSections.add(currentSection);
            currentStation = currentSection.getDownStationId();
        }
        this.sections = orderedSections;
    }

    private Map<Long, Section> generateConnection() {
        Map<Long, Section> connection = new HashMap<>();
        for (Section section : sections) {
            connection.put(section.getUpStationId(), section);
        }
        return connection;
    }
}
