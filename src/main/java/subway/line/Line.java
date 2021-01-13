package subway.line;

import subway.station.Station;

import java.util.List;

public class Line {
    private long id;
    private String name;
    private String color;
    private List<Station> stations;

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
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

    public List<Station> getStations() {
        return stations;
    }
}
