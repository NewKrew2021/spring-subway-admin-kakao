package subway.line.vo;

import subway.line.dto.LineRequest;

public class LineCreateValue {
    private final String name;
    private final String color;

    public LineCreateValue(LineRequest lineRequest) {
        name = lineRequest.getName();
        color = lineRequest.getColor();
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}
