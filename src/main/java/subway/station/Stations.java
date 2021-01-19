package subway.station;

import java.util.List;
import java.util.stream.Collectors;

public class Stations {
    private final List<Station> stations;

    public Stations(List<Station> stations) {
        validateDuplicate(stations);
        this.stations = stations;
    }

    private void validateDuplicate(List<Station> stations) {
        long count = stations.stream().distinct().count();
        if (count != stations.size()) {
            throw new IllegalArgumentException("There are some duplicate stations.");
        }
    }

    public boolean hasDuplicate(Station station) {
        return stations.stream()
                .filter(_station -> _station.equals(station))
                .collect(Collectors.toList())
                .size() > 0;
    }

    public List<StationResponse> toDto() {
        return stations.stream()
                .map(Station::toDto)
                .collect(Collectors.toList());
    }
}
