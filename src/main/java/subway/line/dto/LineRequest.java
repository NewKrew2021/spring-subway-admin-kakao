package subway.line.dto;

import com.fasterxml.jackson.annotation.JsonCreator;

public class LineRequest {
    private String name;
    private String color;
    private Long upStationID;
    private Long downStationID;
    private int distanceDiff;
    private int extraFare;

    @JsonCreator
    public LineRequest(String name, String color, Long upStationID, Long downStationID, int distanceDiff) {
        this.name = name;
        this.color = color;
        this.upStationID = upStationID;
        this.downStationID = downStationID;
        this.distanceDiff = distanceDiff;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Long getUpStationID() {
        return upStationID;
    }

    public Long getDownStationID() {
        return downStationID;
    }

    public int getDistanceDiff() {
        return distanceDiff;
    }

    public int getExtraFare() {
        return extraFare;
    }
}
