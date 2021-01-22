package subway.line.domain;

public class Line {

    private Long id;
    private int extraFare;
    private String color;
    private String name;

    public Line(){}

    public Line(Long id){
        this.id = id;
    }

    public Line(Long id, String name, String color, int extraFare) {
        this(name, color, extraFare);
        this.id = id;
    }

    public Line(String name, String color, int extraFare) {
        this.name = name;
        this.color = color;
        this.extraFare = extraFare;
    }

    public String getColor() {
        return color;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getExtraFare() { return extraFare;}

}
