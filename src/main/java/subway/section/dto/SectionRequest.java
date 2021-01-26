package subway.section.dto;

import subway.section.domain.SectionType;

public class SectionRequest {
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

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

    public Long getInsertStationId(SectionType type) {
        if (type == SectionType.INSERT_UP_STATION) {
            return getUpStationId();
        }
        return getDownStationId();
    }


}
