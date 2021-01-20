package subway.section.presentation;

import subway.section.domain.SectionCreateValue;

public class SectionRequest {
    private Long upStationId;
    private Long downStationId;
    private int distance;

    SectionRequest() {
    }

    public SectionRequest(Long upStationId, Long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public SectionCreateValue toCreateValue(Long lineId) {
        return new SectionCreateValue(lineId, upStationId, downStationId, distance);
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
