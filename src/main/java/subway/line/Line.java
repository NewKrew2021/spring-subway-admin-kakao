package subway.line;

import subway.section.Section;

import java.util.Objects;

public class Line {

    public static final int END_STATION_SECTION_SIZE = 1;

    private Long id;
    private String name;
    private String color;
    private int extraFare;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public Line(Long id, String name, String color, Long upStationId, Long downStationId, int distance) {
        this.id = id;
        this.name = name;
        this.color = color;
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

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public int getExtraFare() {
        return extraFare;
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
        if (stationId == endSection.getUpStationId()) {
            this.upStationId = endSection.getDownStationId();
        }

        if (stationId == endSection.getDownStationId()) {
            this.downStationId = endSection.getUpStationId();
        }
    }

    public static Line getLineToLineRequest(Long id, LineRequest lineRequest) {
        return new Line(id, lineRequest.getName(), lineRequest.getColor(),
                lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance());
    }
}
