package subway.domain;

import subway.dto.LineRequest;

public class Line {
    private Long id;
    private final String name;
    private final String color;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public Line(Long id, String name, String color, Long upStationId, Long downStationId) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
    }

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

    public long getId() {
        return id;
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

    public void setUpStationId(Long upStationId) {
        this.upStationId = upStationId;
    }

    public void setDownStationId(Long downStationId) {
        this.downStationId = downStationId;
    }

    public boolean isSectionContainUpEndStation(Section section){
        if(section == null) return false;
        return section.getUpStationId().equals(this.upStationId);
    }
    public boolean isSectionContainEndDownStation(Section section){
        if(section == null) return false;
        return section.getUpStationId().equals(this.downStationId);
    }

    public boolean isMatchedOnlyUpEndStation(Section section) {
        return this.getUpStationId().equals(section.getDownStationId());
    }

    public boolean isMatchedOnlyDownEndStation(Section section) {
        return this.getDownStationId().equals(section.getUpStationId());
    }
}
