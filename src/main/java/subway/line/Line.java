package subway.line;

public class Line {
    public static final long HEAD = 0;
    public static final long TAIL = -1;

    private final Long id;
    private final String name;
    private final String color;

    public Line(Long id, LineRequest lineRequest) {
        this.id = id;
        this.name = lineRequest.getName();
        this.color = lineRequest.getColor();
    }

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
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
