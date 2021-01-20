package subway.line;

import java.beans.ConstructorProperties;

public class SectionRequest {
    private Long upStationId;
    private Long downStationId;
    private int distance;

    @ConstructorProperties({"upStationId", "downStationId", "distance"})
    public SectionRequest(Long upStationId, Long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
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
