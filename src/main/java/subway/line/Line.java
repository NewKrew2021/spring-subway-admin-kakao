package subway.line;

public class Line {
    private Long id;
    private String name;
    private String color;
    private Long startStationId;

    public Line() {
    }

    public Line(Long id, String name, String color, Long startStationId) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.startStationId = startStationId;
    }

    public Line(String name, String color, Long startStationId) {
        this.name = name;
        this.color = color;
        this.startStationId = startStationId;
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
}
