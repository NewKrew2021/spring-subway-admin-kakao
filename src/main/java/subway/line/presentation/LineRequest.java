package subway.line.presentation;

import subway.line.domain.LineCreateValue;
import subway.section.domain.SectionCreateValue;

public class LineRequest {
    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    LineRequest() {
    }

    public LineRequest(String name, String color, Long upStationId, Long downStationId, int distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public LineCreateValue toCreateValue() {
        return new LineCreateValue(name, color);
    }

    public SectionCreateValue.Pending toPendingSectionCreateValue() {
        return new SectionCreateValue.Pending(upStationId, downStationId, distance);
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }
}
