package subway.domain.station;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class Stations {
    private List<Station> stations;

    public Stations(List<Station> stations) {
        this.stations = stations;
    }

    public boolean contain(Long stationId) {
        return stations.stream()
                .anyMatch(station -> station.getId().equals(stationId));
    }

    public Stream<Station> stream() {
        return stations.stream();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stations stations1 = (Stations) o;
        return Objects.equals(stations, stations1.stations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stations);
    }

    public boolean equalContainStatus(Long firstId, Long secondId) {
        return contain(firstId) == contain(secondId);
    }
}
