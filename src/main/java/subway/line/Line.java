package subway.line;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Line {
    private Long id;
    private String color;
    private String name;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public Line() {
    }

    public Line(String name, String color, Long upStationId, Long downStationId, int distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public String getColor() {
        return color;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Long> getStationInfo() {
        return Arrays.asList(upStationId, downStationId);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return distance == line.distance &&
                Objects.equals(color, line.color) &&
                Objects.equals(name, line.name) &&
                Objects.equals(upStationId, line.upStationId) &&
                Objects.equals(downStationId, line.downStationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, name, upStationId, downStationId, distance);
    }
}
