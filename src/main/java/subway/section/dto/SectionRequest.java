package subway.section.dto;

import com.fasterxml.jackson.annotation.JsonCreator;

public class SectionRequest {
    private Long upStationID;
    private Long downStationID;
    private int distanceDiff;

    @JsonCreator
    public SectionRequest(Long upStationID, Long downStationID, int distanceDiff) {
        this.upStationID = upStationID;
        this.downStationID = downStationID;
        this.distanceDiff = distanceDiff;
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
}
