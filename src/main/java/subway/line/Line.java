package subway.line;

public class Line {
    private Long id;
    private String name;
    private String color;

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(Long id, String name, String color){
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public Line(LineDto lineDto){
        this.name = lineDto.getName();
        this.color = lineDto.getColor();
    }

    public Line(Long id, LineDto lineDto){
        this.id = id;
        this.name = lineDto.getName();
        this.color = lineDto.getColor();
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
