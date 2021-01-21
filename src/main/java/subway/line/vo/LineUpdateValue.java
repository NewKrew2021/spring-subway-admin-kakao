package subway.line.vo;

public class LineUpdateValue {
    private final long id;
    private final String name;
    private final String color;

    public LineUpdateValue(long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public long getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}
