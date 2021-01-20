package subway.station.presentation;

import subway.station.domain.StationCreateValue;

public class StationRequest {
    private String name;

    StationRequest() {
    }

    public StationRequest(String name) {
        this.name = name;
    }

    public StationCreateValue toCreateValue() {
        return new StationCreateValue(name);
    }

    public String getName() {
        return name;
    }
}
