package subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Line {
    // TODO 확인
    private final List<Station> stations = new ArrayList<>();
    private Long id;
    private String name;
    private String color;

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addStation(Station station) {
        stations.add(station);
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<Station> getStations() {
        return stations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(stations, line.stations) && Objects.equals(name, line.name) && Objects.equals(color, line.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stations, name, color);
    }
}
