package subway.line.vo;

import subway.line.dto.LineRequest;

public class LineAttributes {
    private final String name;
    private final String color;

    public LineAttributes(LineRequest lineRequest) {
        this.name = lineRequest.getName();
        this.color = lineRequest.getColor();
    }

    public String getName() {
        return name;
    }
    
    public String getColor() {
        return color;
    }
}
