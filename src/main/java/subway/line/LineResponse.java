package subway.line;

import com.fasterxml.jackson.annotation.JsonCreator;
import subway.station.Station;
import subway.station.StationResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private int extraFare;
    private List<StationResponse> stations;

    @JsonCreator
    public LineResponse(Long id, String name, String color, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public LineResponse(Line line) {
        this(line.getId(), line.getName(), line.getColor(), new ArrayList<StationResponse>());
    }

    public LineResponse(Line line, List<Station> stations) {
        this(line.getId(), line.getName(), line.getColor(), stations.stream()
                .map(StationResponse::new)
                .collect(Collectors.toList()));
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
