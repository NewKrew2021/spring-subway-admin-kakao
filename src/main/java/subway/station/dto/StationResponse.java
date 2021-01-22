package subway.station.dto;

import com.fasterxml.jackson.annotation.JsonCreator;

public class StationResponse {
    private Long id;
    private String name;

    @JsonCreator
    public StationResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getID() {
        return id;
    }

    public String getName() {
        return name;
    }
}
