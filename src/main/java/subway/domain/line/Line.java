package subway.domain.line;

import subway.domain.section.Sections;
import subway.domain.station.Stations;

import java.util.Objects;

public class Line {
    private Long id;
    private String name;
    private String color;
    private Sections sections;

    public Line(Long id, String name, String color) {
        this(id, name, color, null);
    }

    public Line(String name, String color) {
        this(null, name, color, null);
    }

    public Line(Line line, Sections sections) {
        this(line.getId(), line.getName(), line.getColor(), sections);
    }

    public Line(Long id, String name, String color, Sections sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = sections;
    }

    public Stations getAllStations() {
        return sections.getAllStations();
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
        return Objects.equals(name, line.name) && Objects.equals(color, line.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, color);
    }
}
