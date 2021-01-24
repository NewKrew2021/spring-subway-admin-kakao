package subway.station.vo;

import subway.station.domain.Station;
import subway.station.dto.StationResponse;

public class StationResultValue {
    private final long id;
    private final String name;

    public StationResultValue(Station station) {
        this.id = station.getID();
        this.name = station.getName();
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
