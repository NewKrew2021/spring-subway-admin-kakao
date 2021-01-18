package subway.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import subway.domain.Line;

import javax.validation.constraints.*;

public class LineRequest {
    @Pattern(regexp = "^[가-힣a-zA-Z]{2,255}$")
    @NotEmpty
    private final String name;
    @Size(min = 1, max = 20)
    @NotEmpty
    private final String color;
    @Positive
    @NotNull
    private final Long upStationId;
    @Positive
    @NotNull
    private final Long downStationId;
    @Min(1)
    @Max(100000)
    private final int distance;

    public LineRequest(@JsonProperty("name") String name,
                       @JsonProperty("color") String color,
                       @JsonProperty("upStationId") Long upStationId,
                       @JsonProperty("downStationId") Long downStationId,
                       @JsonProperty("distance") int distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Line getDomain() {
        return new Line(name, color);
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
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
