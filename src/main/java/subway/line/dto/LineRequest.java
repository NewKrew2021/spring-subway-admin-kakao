package subway.line.dto;

import com.fasterxml.jackson.annotation.JsonCreator;

public class LineRequest {
    private String name;
    private String color;
    private Long upStationID;
    private Long downStationID;
    private int distance;
    private int extraFare;

    @JsonCreator
    public LineRequest(String name, String color, Long upStationID, Long downStationID, int distance) {
        this.name = name;
        this.color = color;
        this.upStationID = upStationID;
        this.downStationID = downStationID;
        this.distance = distance;
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

    public int getDistance() {
        return distance;
    }

    public int getExtraFare() {
        return extraFare;
    }
}
