package subway.domain;

import subway.dto.LineRequest;

import java.util.Objects;

public class Line {
    private Long id;
    private String name;
    private String color;

    private Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    private Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public static Line of(LineRequest lineRequest) {
        return new Line(lineRequest.getName(), lineRequest.getColor());
    }

    public static Line of(Long id, String name, String color) {
        return new Line(id, name, color);
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

    public void updateNameAndColor(String name, String color) {
        this.name = name;
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(id, line.id) && Objects.equals(name, line.name) && Objects.equals(color, line.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color);
    }
}
