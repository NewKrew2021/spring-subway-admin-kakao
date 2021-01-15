package subway.line;

public class Line {
    private Long id;
    private String name;
    private String color;
    private Long startStationId;
    private Long endStationId;

    public Line() {
    }

    public Line(Long id, String name, String color, Long startStationId, Long endStationId) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.startStationId = startStationId;
        this.endStationId = endStationId;
    }

    public Line(String name, String color, Long startStationId, Long endStationId) {
        this.name = name;
        this.color = color;
        this.startStationId = startStationId;
        this.endStationId = endStationId;
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
}
