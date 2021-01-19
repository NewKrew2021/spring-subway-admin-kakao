package subway.station;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Stations {
    private final List<Station> stations;

    public Stations(List<Station> stations) {
        this.stations = Collections.unmodifiableList(stations);

        if (!(areValidNames() && areValidIDs())) {
            throw new IllegalArgumentException("There cannot be multiple stations with same name or ID");
        }
    }

    public List<StationResponse> allToDto() {
        return stations.stream()
                .map(Station::toDto)
                .collect(Collectors.toList());
    }

    private boolean areValidNames() {
        return stations.stream()
                .map(Station::getName)
                .distinct()
                .count() == stations.size();
    }

    private boolean areValidIDs() {
        return stations.stream()
                .map(Station::getID)
                .distinct()
                .count() == stations.size();
    }
}
