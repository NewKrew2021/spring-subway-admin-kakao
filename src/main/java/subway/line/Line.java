package subway.line;

import subway.station.Station;
import subway.station.StationResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Line {
    private Long id;
    private String name;
    private String color;
    private int extraFare;
    private List<Station> stations;

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
        this.stations = new ArrayList<>();
    }

    public void update(LineRequest lineRequest) {
        if (lineRequest.getName() != null) {
            this.name = lineRequest.getName();
        }
        if (lineRequest.getColor() != null) {
            this.color = lineRequest.getColor();
        }
    }

    public List<StationResponse> getStationResponses() {
        return stations.stream()
                .map(station -> new StationResponse(station.getId(), station.getName()))
                .collect(Collectors.toList());
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

    public int getExtraFare() {
        return extraFare;
    }

    public List<Station> getStations() {
        return stations;
    }

    @Override
    public String toString() {
        return "Line{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", extraFare=" + extraFare +
                '}';
    }

}
