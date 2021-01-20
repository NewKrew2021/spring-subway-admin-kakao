package subway.station.presentation;

import subway.station.domain.Station;

public class StationRequest {
    private String name;

    StationRequest() {
    }

    public StationRequest(String name) {
        this.name = name;
    }

    public Station toEntity() {
        return new Station(name);
    }

    public String getName() {
        return name;
    }
}
