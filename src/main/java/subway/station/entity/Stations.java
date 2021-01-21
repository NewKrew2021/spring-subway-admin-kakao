package subway.station.entity;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class Stations {
    private final List<Station> stations;

    public Stations(List<Station> stations) {
        this.stations = Collections.unmodifiableList(stations);
    }

    public Stream<Station> stream() {
        return stations.stream();
    }
}
