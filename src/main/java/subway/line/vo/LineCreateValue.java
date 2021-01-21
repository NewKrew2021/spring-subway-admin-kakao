package subway.line.vo;

public class LineCreateValue {
    private final String name;
    private final String color;

    public LineCreateValue(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}
