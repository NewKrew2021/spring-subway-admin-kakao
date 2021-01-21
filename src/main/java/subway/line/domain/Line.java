package subway.line.domain;

import subway.line.presentation.LineRequest;

public class Line {

    private Long id;
    private String name;
    private String color;
    private int extraFare;

    public Line(Long id, String name, String color, int extraFare) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.extraFare = extraFare;
    }

    public Line(Long id, LineRequest lineRequest) {
        this.id = id;
        this.name = lineRequest.getName();
        this.color = lineRequest.getColor();
        this.extraFare = lineRequest.getExtraFare();
    }

    public Line (LineRequest lineRequest) {
        this.name = lineRequest.getName();
        this.color = lineRequest.getColor();
        this.extraFare = lineRequest.getExtraFare();
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

    public int getExtraFare() { return extraFare;
    }

}
