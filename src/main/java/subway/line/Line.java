package subway.line;

import org.apache.commons.lang3.StringUtils;
import subway.station.domain.Stations;
import subway.station.vo.StationResultValue;

import java.util.stream.Collectors;

public class Line {
    private final Long id;
    private final String name;
    private final String color;

    public Line(String name, String color) {
        this(0L, name, color);
    }

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;

        checkAreValidArgument();
    }

    public LineResponse toResultValue(Stations stations) {
        return new LineResponse(id, name, color, stations.allToResultValues()
                .stream()
                .map(StationResultValue::toResponse)
                .collect(Collectors.toList()));
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

    private void checkAreValidArgument() {
        if (isNegativeID()) {
            throw new IllegalArgumentException("Line ID cannot be negative");
        }

        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Line name should not be null or blank");
        }

        if (StringUtils.isBlank(color)) {
            throw new IllegalArgumentException("Line color should not be null or blank");
        }
    }

    private boolean isNegativeID() {
        return id < 0;
    }
}
