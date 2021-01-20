package subway.line;

import java.beans.ConstructorProperties;

public class LineRequest {
    private String name;
    private String color;
    private Long upStationID;
    private Long downStationID;
    private int distance;
    private int extraFare;

    @ConstructorProperties({"name", "color", "upStationID", "downStationID", "distance"})
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
