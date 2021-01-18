package subway.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import subway.domain.Station;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

public class StationRequest {
    @Pattern(regexp = "^[가-힣a-zA-Z]{1,255}$")
    @NotEmpty
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
