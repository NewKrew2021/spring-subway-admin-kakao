package subway.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import subway.domain.Section;

public class SectionRequest {
    private final Long lineId;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    public SectionRequest(@JsonProperty("lineId") Long lineId,
                          @JsonProperty("upStationId") Long upStationId,
                          @JsonProperty("downStationId") Long downStationId,
                          @JsonProperty("distance") int distance) {
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section getDomain(){
        return new Section(lineId, upStationId, downStationId, distance);
    }

    public Long getLineId() {
        return lineId;
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
