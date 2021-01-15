package subway.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SectionRequest {
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    public SectionRequest(@JsonProperty("upStationId") Long upStationId,
                          @JsonProperty("downStationId") Long downStationId,
                          @JsonProperty("distance") int distance) {
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
