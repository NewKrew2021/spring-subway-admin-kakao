package subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StationResponses {
    private final List<StationResponse> stationResponses;

    public StationResponses(List<Station> stations) {
        stationResponses=new ArrayList<>();
        for (Station station : stations) {
            stationResponses.add(new StationResponse(station));
        }
    }

    public List<StationResponse> getStationResponses() {
        return Collections.unmodifiableList(stationResponses);
    }
}
