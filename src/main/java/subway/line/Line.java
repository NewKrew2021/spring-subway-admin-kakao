package subway.line;

import org.apache.commons.lang3.StringUtils;
import subway.station.Stations;

public class Line {
    private final Long id;
    private final String name;
    private final String color;

    public Line(String name, String color) {
        this(0L, name, color);
    }

    public Line(Long id, String name, String color) {
        checkHasInvalidArgument(id, name, color);

        this.id = id;
        this.name = name;
        this.color = color;
    }

    public LineResponse toDto(Stations stations) {
        return new LineResponse(id, name, color, stations.allToDto());
    }

    public Long getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    private void checkHasInvalidArgument(Long id, String name, String color) {
        if (isNegative(id)) {
            throw new IllegalArgumentException("Line ID cannot be negative");
        }

        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Line name should not be null or blank");
        }

        if (StringUtils.isBlank(color)) {
            throw new IllegalArgumentException("Line color should not be null or blank");
        }
    }

    private boolean isNegative(Long id) {
        return id < 0;
    }
}
