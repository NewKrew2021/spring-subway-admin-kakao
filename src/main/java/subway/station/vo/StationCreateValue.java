package subway.station.vo;

import subway.station.dto.StationRequest;

public class StationCreateValue {
    private final String name;

    public StationCreateValue(StationRequest request) {
        name = request.getName();
    }

    public String getName() {
        return name;
    }
}
