package subway.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StationRequest {
    private final String name;

    public StationRequest(@JsonProperty("name") String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
