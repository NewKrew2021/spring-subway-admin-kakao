package subway.dto;

import subway.http.request.LineRequest;

public class LineDto {
    private final String name;
    private final String color;

    public LineDto(LineRequest request) {
        this.name = request.getName();
        this.color = request.getColor();
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}
