package subway.line;

public class Line {
    private Long id;
    private String name;
    private String color;

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color, Long id){
        this.name = name;
        this.color = color;
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
}
