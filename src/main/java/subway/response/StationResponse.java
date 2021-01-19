package subway.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import subway.domain.Station;

public class StationResponse {
    private final Long id;
    private final String name;

    public StationResponse(Station station) {
        this(station.getId(), station.getName());
    }

    public StationResponse(@JsonProperty("id") Long id,
                           @JsonProperty("name") String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
