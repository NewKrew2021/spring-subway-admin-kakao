package subway.station.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import subway.station.domain.Station;

public class StationResponse {
    private Long id;
    private String name;

    @JsonCreator
    public StationResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static StationResponse of(Station station) {
        return new StationResponse(station.getID(), station.getName());
    }

    public Long getID() {
        return id;
    }

    public String getName() {
        return name;
    }
}
