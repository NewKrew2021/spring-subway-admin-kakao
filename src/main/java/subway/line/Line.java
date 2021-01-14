package subway.line;

import subway.station.Station;

import java.util.List;

public class Line {
    private Long id;
    private String color;
    private String name;
    private Long upStationId;
    private Long downStationId;
    private Integer distance;
    private List<Station> stations;

    public Line() {
    }

    public Line(String color, String name) {
        this.color = color;
        this.name = name;
    }

    public Line(Long id, String color, String name) {
        this.id = id;
        this.color = color;
        this.name = name;
    }

    public Line(String color, String name, Long upStationId, Long downStationId, Integer distance) {
        this.color = color;
        this.name = name;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public String getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public List<Station> getStations() {
        return stations;
    }
}
