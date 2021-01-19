package subway.line;

import subway.station.StationResponse;

import java.util.List;

public class Line {
    private Long id;
    private String name;
    private String color;
    private Long startStationId;
    private Long endStationId;

    public Line(Long id, String name, String color, Long startStationId, Long endStationId) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.startStationId = startStationId;
        this.endStationId = endStationId;
    }

    public Line(String name, String color, Long startStationId, Long endStationId) {
        this(null, name, color, startStationId, endStationId);
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

    public LineResponse makeLineResponse(List<StationResponse> stations) {
        return new LineResponse(getId(), getName(), getColor(), stations);
    }

    public Line getLineEndStationChanged(long newEndStationId) {
        return new Line(id, name, color, startStationId, newEndStationId);
    }

    public Line getLineStartStationChanged(long newStartStationId) {
        return new Line(id, name, color, newStartStationId, endStationId);
    }

    public boolean isStartStation(long stationId) {
        return stationId == startStationId;
    }

    public boolean isEndStation(long stationId) {
        return stationId == endStationId;
    }
}
