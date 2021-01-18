package subway.station;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Stations {
    private final List<Station> stations;

    public Stations(List<Station> stations) {
        int originalCount = stations.size();
        boolean isValid = stations.stream()
                .distinct()
                .count() == originalCount;

        if (!isValid) {
            throw new IllegalArgumentException("There cannot be multiple stations with same name or ID");
        }

        this.stations = Collections.unmodifiableList(stations);
    }

    public List<StationResponse> allToDto() {
        return stations.stream()
                .map(Station::toDto)
                .collect(Collectors.toList());
    }
}
