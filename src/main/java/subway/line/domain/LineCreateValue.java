package subway.line.domain;

public class LineCreateValue {

    private final String name;
    private final String color;

    public LineCreateValue(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line toEntity() {
        return new Line(name, color);
    }

    public Line toEntity(Long id) {
        return new Line(id, name, color);
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}

