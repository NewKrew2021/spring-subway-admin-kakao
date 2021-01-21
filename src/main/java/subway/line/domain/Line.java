package subway.line.domain;

import org.apache.commons.lang3.StringUtils;
import subway.line.dto.LineResponse;
import subway.station.domain.Stations;

import java.util.Objects;

public class Line {
    private final Long id;
    private final String name;
    private final String color;

    private static final Long MINIMUM_ID = 0L;

    public Line(Long id, String name, String color) {
        validateId(id);
        validateName(name);

        this.id = id;
        this.name = name;
        this.color = color;
    }

    private void validateId(Long id) {
        if (id < MINIMUM_ID) {
            throw new IllegalArgumentException("Line ID cannot be negative");
        }
    }

    private void validateName(String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Line name cannot be null or blank characters.");
        }
    }

    public LineResponse toDto(Stations stations) {
        return new LineResponse(id, name, color, stations.toDto());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Line line = (Line) o;
        return Objects.equals(name, line.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color);
    }


}
