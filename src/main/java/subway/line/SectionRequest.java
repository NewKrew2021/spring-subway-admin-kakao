package subway.line;

import java.beans.ConstructorProperties;

public class SectionRequest {
    private Long upStationID;
    private Long downStationID;
    private int distance;

    @ConstructorProperties({"upStationID", "downStationID", "distance"})
    public SectionRequest(Long upStationID, Long downStationID, int distance) {
        this.upStationID = upStationID;
        this.downStationID = downStationID;
        this.distance = distance;
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
}
