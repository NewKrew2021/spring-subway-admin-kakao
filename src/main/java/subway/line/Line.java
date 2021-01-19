package subway.line;

import java.util.Objects;

public class Line {

    private long id;
    private final String name;
    private final String color;
    private final long startStationId;
    private final long endStationId;

    public Line(String name, String color, long startStationId, long endStationId) {
        this.name = name;
        this.color = color;
        this.startStationId = startStationId;
        this.endStationId = endStationId;
    }

    public Line(long id, String name, String color, long startStationId, long endStationId) {
        this(name, color, startStationId, endStationId);
        this.id = id;
    }

    public boolean isLineStartStation(long stationId) {
        return startStationId == stationId;
    }

    public boolean isLineEndStation(long stationId) {
        return endStationId == stationId;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public long getStartStationId() { return startStationId; }

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
