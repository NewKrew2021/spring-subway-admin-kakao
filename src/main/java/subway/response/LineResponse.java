package subway.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import subway.domain.Line;
import subway.domain.Station;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class LineResponse {
    private final Long id;
    private final String name;
    private final String color;
    // private final int extraFare;
    private final List<StationResponse> stations;

    public LineResponse(Line line, List<Station> stations) {
        this(line.getId(), line.getName(), line.getColor(),
                Collections.unmodifiableList(
                        stations.stream()
                                .map(StationResponse::new)
                                .collect(Collectors.toList()))
        );
    }

    public LineResponse(@JsonProperty("id") Long id,
                        @JsonProperty("name") String name,
                        @JsonProperty("color") String color,
                        @JsonProperty("stations") List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<StationResponse> getStations() {
        return stations;
    }
}
