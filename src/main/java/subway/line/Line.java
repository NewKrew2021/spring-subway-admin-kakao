package subway.line;

import java.util.List;
import java.util.Objects;

public class Line {

    private final int END_STATION_SECTION_SIZE = 1;

    private Long id;
    private String name;
    private String color;
    private int extraFare;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(Long id, Long upStationId, Long downStationId, int distance) {
        this.id = id;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Line(LineRequest lineRequest) {
        this.name = lineRequest.getName();
        this.color = lineRequest.getColor();
        this.upStationId = lineRequest.getUpStationId();
        this.downStationId = lineRequest.getDownStationId();
        this.distance = lineRequest.getDistance();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getExtraFare() {
        return extraFare;
    }

    public void setExtraFare(int extraFare) {
        this.extraFare = extraFare;
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

    public void update(LineRequest lineRequest) {
        this.name = lineRequest.getName();
        this.color = lineRequest.getColor();
    }

    public void update(Line line) {
        this.upStationId = line.getUpStationId();
        this.downStationId = line.getDownStationId();
        this.distance = line.getDistance();

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
        return Objects.hash(id, name, color, extraFare, upStationId, downStationId, distance);
    }

    public boolean isEndStation(int sectionListSize) {
        return sectionListSize == END_STATION_SECTION_SIZE;
    }

    public void updateEndStation(Section endSection, Long stationId) {
        if(stationId == this.upStationId){
            this.upStationId = endSection.getDownStationId();
        }

        if(stationId == this.downStationId){
            this.downStationId = endSection.getUpStationId();
        }
    }
}
