package subway.line;

public class Line {

    private Long id;
    private String name;
    private String color;

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public Line(Long id, LineRequest lineRequest) {
        this.id = id;
        this.name = lineRequest.getName();
        this.color = lineRequest.getColor();
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

}
