package subway.line;

public class Line {
    private Long id;
    private String name;
    private String color;
    private long upTerminalStationId;
    private long downTerminalStationId;

    public Line(Long id, String name, String color, long upTerminalStationId, long downTerminalStationId) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.upTerminalStationId = upTerminalStationId;
        this.downTerminalStationId = downTerminalStationId;
    }

    public Line(String name, String color, long upTerminalStationId, long downTerminalStationId) {
        this.name = name;
        this.color = color;
        this.upTerminalStationId = upTerminalStationId;
        this.downTerminalStationId = downTerminalStationId;
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

    public long getUpTerminalStationId() {
        return upTerminalStationId;
    }

    public long getDownTerminalStationId() {
        return downTerminalStationId;
    }
}
