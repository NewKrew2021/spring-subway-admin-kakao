package subway.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import subway.domain.Section;

public class SectionResponse {
    private final Long id;
    private final Long lineId;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    public SectionResponse(Section section) {
        this(section.getId(), section.getLineId(), section.getUpStationId(),
                section.getDownStationId(), section.getDistance());
    }

    public SectionResponse(@JsonProperty("id") Long id,
                           @JsonProperty("lineId") Long lineId,
                           @JsonProperty("upStationId") Long upStationId,
                           @JsonProperty("downStationId") Long downStationId,
                           @JsonProperty("distance") int distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Long getId() {
        return id;
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
