package subway.line;

import subway.station.StationResponse;

import java.util.List;
import java.util.Objects;

public class Line {
    private Long id;
    private String name;
    private String color;

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public LineResponse toDto(List<StationResponse> stations) {
        return new LineResponse(id, name, color, stations);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(name, line.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color);
    }
}
