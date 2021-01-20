package subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Stations {
    private final List<Station> stations;

    public Stations(List<Station> stations) {
        this.stations = stations;
    }

    public List<Station> getStations(){
        return Collections.unmodifiableList(stations);
    }

}
