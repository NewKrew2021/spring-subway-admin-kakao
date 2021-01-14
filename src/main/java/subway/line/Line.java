package subway.line;

import subway.station.Station;

import java.util.List;

public class Line {
    private Long id;
    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private int distance;
    private List<Station> stations;

    public Line() {
    }

    public Line(String color, String name) {
        this.id = 0L;
        this.name = name;
        this.color = color;
        this.upStationId = 0L;
        this.downStationId = 0L;
        this.distance = 0;
    }

    public Line(Long id, String name, String color, Long upStationId, Long downStationId, int distance) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Line(String color, String name, Long upStationId, Long downStationId, int distance, List<Station> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
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

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }

    public List<Station> getStations() {
        return stations;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}
