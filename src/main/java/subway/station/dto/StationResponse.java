package subway.station.dto;

import java.beans.ConstructorProperties;

public class StationResponse {
    private Long id;
    private String name;

    @ConstructorProperties({"id", "name"})
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
