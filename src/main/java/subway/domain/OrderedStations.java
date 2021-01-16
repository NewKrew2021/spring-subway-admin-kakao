package subway.domain;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OrderedStations {
    private final List<Station> orderedStations;

    public OrderedStations() {
        orderedStations = Collections.emptyList();
    }

    public OrderedStations(OrderedSections orderedSections, List<Station> stations) {
        List<Long> orderedStationIds = orderedSections.getOrderedStationIds();
        Map<Long, String> stationNameMap = new HashMap<>();
        stations.forEach(station -> stationNameMap.put(station.getId(), station.getName()));

        orderedStations = orderedStationIds.stream()
                .map(id -> new Station(id, stationNameMap.get(id)))
                .collect(Collectors.toList());
    }

    public List<Station> getOrderedStations() {
        return Collections.unmodifiableList(orderedStations);
    }

    public boolean hasStation(Long stationId) {
        return orderedStations.stream()
                .anyMatch(station -> station.getId().equals(stationId));
    }

    public int size() {
        return orderedStations.size();
    }
}
