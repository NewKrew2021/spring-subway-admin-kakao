package subway.line;

import java.util.Objects;

public class Line {
    private Long id;
    private String name;
    private String color;
    private Long startStationId;
    private Long endStationId;

    public Line() {
    }

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public Line(Long id, Long startStationId, Long endStationId) {
        this.id = id;
        this.startStationId = startStationId;
        this.endStationId = endStationId;
    }

    public Line(String name, String color, Long startStationId, Long endStationId) {
        this.name = name;
        this.color = color;
        this.startStationId = startStationId;
        this.endStationId = endStationId;
    }

    public Line(Long id, String name, String color, Long startStationId, Long endStationId) {
        this(name, color, startStationId, endStationId);
        this.id = id;
    }

    public boolean isStartStation(Long stationId) {
        return startStationId == stationId;
    }

    public boolean isEndStation(Long stationId) {
        return endStationId == stationId;
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

    public Long getStartStationId() {
        return startStationId;
    }

    public Long getEndStationId() {
        return endStationId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(name, line.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
