package subway.dto;

import subway.http.request.LineRequest;
import subway.http.request.SectionRequest;

public class SectionDto {
    private final Long upStationId, downStationId;
    private final int distance;

    public SectionDto(LineRequest lineRequest) {
        this.upStationId = lineRequest.getUpStationId();
        this.downStationId = lineRequest.getDownStationId();
        this.distance = lineRequest.getDistance();
    }

    public SectionDto(SectionRequest sectionRequest) {
        this.upStationId = sectionRequest.getUpStationId();
        this.downStationId = sectionRequest.getDownStationId();
        this.distance = sectionRequest.getDistance();
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
