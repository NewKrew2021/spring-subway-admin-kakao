package subway.line.domain;

import subway.station.domain.Stations;

public class LineResponse {
    private int status;
    private Long id;
    private String name;
    private String color;
    private int extraFare;
    private Stations stations;

    public LineResponse() {
    }

    public LineResponse(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public LineResponse(Long id, String name, String color, Stations stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public LineResponse(Line line) {
        this(line.getId(), line.getName(), line.getColor());
    }

    public LineResponse(Line line, Stations stations) {
        if (!validator(line)) {
            throw new IllegalArgumentException("노선 정보가 존재하지 않습니다.");
        }
        this.id = line.getId();
        this.name = line.getName();
        this.color = line.getColor();
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

    public Stations getStations() {
        return stations;
    }

    private boolean validator(Line line) {
        if (line == null) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "LineResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", stations=" + stations +
                '}';
    }
}
