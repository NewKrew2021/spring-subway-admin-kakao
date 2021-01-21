package subway.station.domain;

import java.util.ArrayList;
import java.util.List;

public class Stations {

    private final List<Station> stations;

    public Stations() {
        stations = new ArrayList<Station>();
    }

    public Stations(List<Station> stations) {
        this.stations = stations;
    }

    public List<Station> getStations() {
        return stations;
    }

    public boolean add(Station station) {
        return stations.add(station);
    }

    public boolean contains(Station station) {
        return stations.contains(station);
    }
}
