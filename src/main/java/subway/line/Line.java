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

    public static Line fromRequest(LineRequest lineRequest) {
        return new Line(lineRequest.getName(),
                lineRequest.getColor(),
                lineRequest.getUpStationId(),
                lineRequest.getDownStationId());
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

    public LineResponse toResponse(List<StationResponse> stations) {
        return new LineResponse(getId(), getName(), getColor(), stations);
    }

    public Line getLineEndStationChanged(long newEndStationId) {
        return new Line(getId(), getName(), getColor(), getStartStationId(), newEndStationId);
    }

    public Line getLineStartStationChanged(long newStartStationId) {
        return new Line(getId(), getName(), getColor(), newStartStationId, getEndStationId());
    }

    public Line getLineNameAndColorChanged(String name, String color) {
        return new Line(getId(), name, color, getStartStationId(), getEndStationId());
    }

    public boolean isStartStation(long stationId) {
        return stationId == startStationId;
    }

    public boolean isEndStation(long stationId) {
        return stationId == endStationId;
    }
}
