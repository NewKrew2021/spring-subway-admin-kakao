package subway.controller.to;

import subway.domain.Station;

public class StationRequest {
    private String name;

    public StationRequest() {
    }

    public StationRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Station getStation() {
        return new Station(name);
    }
}
