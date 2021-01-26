package subway.domain;

import subway.request.LineRequest;

public class Line {
    private Long id;
    private String name;
    private String color;

    public Line() {
    }

    public Line(String name, String color) {
        this();
        this.name = name;
        this.color = color;
    }

    public Line(Long id, String name, String color) {
        this(name, color);
        this.id = id;
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

    public void setPropertyByRequest(LineRequest lineRequest) {
        this.name = lineRequest.getName();
        this.color = lineRequest.getColor();
    }

    @Override
    public String toString() {
        return "Line{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color +
                '}';
    }

}
