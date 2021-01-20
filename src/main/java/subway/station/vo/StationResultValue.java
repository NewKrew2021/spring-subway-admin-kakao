package subway.station.vo;

import subway.station.dto.StationResponse;

public class StationResultValue {
    private final long id;
    private final String name;

    public StationResultValue(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public StationResponse toResponse() {
        return new StationResponse(id, name);
    }

    public long getID() {
        return id;
    }

    public String getName() {
        return name;
    }
}
