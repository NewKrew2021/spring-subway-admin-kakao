package subway.domain.section;

import subway.domain.station.Stations;
import subway.exception.section.IllegalSectionsException;
import subway.exception.section.InvalidStationException;
import subway.exception.section.SectionDeletionException;
import subway.exception.section.SectionSplitException;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Sections {
    private final int MIN_SECTIONS_SIZE = 1;

    private final Map<Long, Section> upStationIdToSectionCache;
    private final Map<Long, Section> downStationIdToSectionCache;

    private List<Section> sections;

    public Sections(Section section) {
        this(Arrays.asList(section));
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
        this.upStationIdToSectionCache = generateConnection(Section::getUpStationId);
        this.downStationIdToSectionCache = generateConnection(Section::getDownStationId);
        sortByOrder();
    }

    private Map<Long, Section> generateConnection(Function<Section, Long> toStationId) {
        return sections.stream()
                .collect(Collectors.toMap(toStationId, Function.identity()));
    }

    private void sortByOrder() {
        List<Section> upside = trackSectionsToMakeList(downStationIdToSectionCache, Section::getUpStationId);
        List<Section> downside = trackSectionsToMakeList(upStationIdToSectionCache, Section::getDownStationId);
        Collections.reverse(upside);
        upside.addAll(downside);
        if (upside.size() > sections.size()) {
            throw new IllegalSectionsException();
        }
        this.sections = upside;
    }

    private List<Section> trackSectionsToMakeList(Map<Long, Section> idToSectionMap, Function<Section, Long> toStationId) {
        List<Section> orderedSections = new ArrayList<>();
        Long current = sections.get(0).getUpStationId();
        for (int i = 0; i < sections.size() && idToSectionMap.containsKey(current); i++) {
            Section currentSection = idToSectionMap.get(current);
            orderedSections.add(currentSection);
            current = toStationId.apply(currentSection);
        }
        return orderedSections;
    }

    public Stations getAllStations() {
        return new Stations(sections.stream()
                .flatMap(section -> section.getStations().stream())
                .distinct()
                .collect(Collectors.toList()));
    }

    public void validateSectionSplit(Section section) {
        Stations stations = getAllStations();
        if (stations.equalContainStatus(section.getUpStationId(), section.getDownStationId())) {
            throw new SectionSplitException();
        }
    }

    public Optional<Section> findSectionToSplit(Section newSection) {
        return Optional.ofNullable(getSectionFromUpStationId(newSection.getUpStationId())
                .orElseGet(() -> getSectionFromDownStationId(newSection.getDownStationId()).orElse(null)));
    }

    private Optional<Section> getSectionFromUpStationId(Long stationId) {
        return Optional.ofNullable(upStationIdToSectionCache.get(stationId));
    }

    private Optional<Section> getSectionFromDownStationId(Long stationId) {
        return Optional.ofNullable(downStationIdToSectionCache.get(stationId));
    }

    public Sections findByStationId(Long stationId) {
        return new Sections(sections.stream()
                .filter(section -> section.contain(stationId))
                .collect(Collectors.toList()));
    }

    public boolean contain(Long stationId) {
        return getAllStations().contain(stationId);
    }

    public void validateDeleteSection(Long stationId) {
        if (!contain(stationId)) {
            throw new InvalidStationException(stationId);
        }
        if (sections.size() == MIN_SECTIONS_SIZE) {
            throw new SectionDeletionException();
        }
    }

    public void forEach(Function<Section, Object> function) {
        sections.forEach(function::apply);
    }

    public Section connect() {
        Section connectedSection = sections.get(0);
        for (int i = 1; i < sections.size(); i++) {
            connectedSection = connectedSection.attach(sections.get(i));
        }
        return connectedSection;
    }
}
