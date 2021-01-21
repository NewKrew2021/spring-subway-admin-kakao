package subway.line;

import java.util.Objects;

public class Line {

    private long id;
    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public Line(long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public Line(LineRequest lineRequest) {
        this.name = lineRequest.getName();
        this.color = lineRequest.getColor();
        this.upStationId = lineRequest.getUpStationId();
        this.downStationId = lineRequest.getDownStationId();
        this.distance = lineRequest.getDistance();
    }


    public Line(String name, String color) {
        this(-1, name, color);
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public void setUpStationId(Long upStationId) {
        this.upStationId = upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public void setDownStationId(Long downStationId) {
        this.downStationId = downStationId;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(name, line.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
