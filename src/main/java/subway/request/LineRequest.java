package subway.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import subway.domain.Line;

public class LineRequest {
    private final String name;
    private final String color;
    private final Long upStationId;
    private final Long downStationId;
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
