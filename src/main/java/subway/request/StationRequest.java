package subway.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import subway.domain.Station;

public class StationRequest {
    private final String name;

    public StationRequest(@JsonProperty("name") String name) {
        this.name = name;
    }

    public Station getDomain() {
        return new Station(name);
    }

    public String getName() {
        return name;
    }
}
