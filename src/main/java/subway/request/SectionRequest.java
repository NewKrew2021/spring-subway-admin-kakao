package subway.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import subway.domain.Section;
import subway.exception.custom.SameUpstationDownStationException;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class SectionRequest {
    @Positive
    @NotNull
    private final Long lineId;
    @Positive
    @NotNull
    private final Long upStationId;
    @Positive
    @NotNull
    private final Long downStationId;
    @Min(1)
    @Max(100000)
    private final int distance;

    public SectionRequest(@JsonProperty("lineId") Long lineId,
                          @JsonProperty("upStationId") Long upStationId,
                          @JsonProperty("downStationId") Long downStationId,
                          @JsonProperty("distance") int distance) {
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
        validateSection(upStationId, downStationId);
    }

    private void validateSection(Long upStationId, Long downStationId) {
        if (upStationId.equals(downStationId)) {
            throw new SameUpstationDownStationException();
        }
    }

    public Section getDomain() {
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
