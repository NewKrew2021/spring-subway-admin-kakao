package subway.response;

import subway.domain.Line;
import subway.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private int extraFare;
    private List<StationResponse> stationResponses;

    public LineResponse() {
    }

    public LineResponse(Line line) {
        this.id = line.getId();
        this.name = line.getName();
        this.color = line.getColor();
    }

    public LineResponse(Line line, List<StationResponse> stationResponses) {
        this(line);
        this.stationResponses = stationResponses;
    }

    public static LineResponse createWithStations(Line line, List<Station> stations) {
        return new LineResponse(line, stations.stream()
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

    public List<StationResponse> getStationResponses() {
        return stationResponses;
    }
}
