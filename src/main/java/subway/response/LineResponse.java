package subway.response;

import subway.domain.Line;
import subway.domain.Station;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private int extraFare;
    private List<StationResponse> stations;

    // TODO 뭔진 모르겠지만 default 생성자가 필요.
    public LineResponse() {
    }

    public LineResponse(Long id, String name, String color, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = Collections.unmodifiableList(stations);
    }

    public LineResponse(Line line, List<Station> stations) {
        this.id = line.getId();
        this.name = line.getName();
        this.color = line.getColor();

        List<StationResponse> stationResponses = stations.stream().map(StationResponse::new).collect(Collectors.toList());
        this.stations = Collections.unmodifiableList(stationResponses);
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

    private List<StationResponse> sectionToStation(List<Station> stations) {
        return stations.stream().map(StationResponse::new).collect(Collectors.toList());
    }
}
