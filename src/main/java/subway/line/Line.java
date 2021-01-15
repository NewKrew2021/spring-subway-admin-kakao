package subway.line;

import subway.station.Station;

import java.util.ArrayList;
import java.util.List;

public class Line {
    private Long id;
    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private int distance;
    private List<Station> stations;
    private List<Section> sections;

    public Line() {
        stations = new ArrayList<>();
        sections = new ArrayList<>();
    }

    public Line(String color, String name) {
        this();
        this.id = 0L;
        this.name = name;
        this.color = color;
        this.upStationId = 0L;
        this.downStationId = 0L;
        this.distance = 0;
    }

    public Line(Long id, String name, String color, Long upStationId, Long downStationId, int distance) {
        this();
        this.id = id;
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Line(String color, String name, Long upStationId, Long downStationId, int distance, List<Station> stations) {
        this();
        this.id = id;
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
        this.stations = stations;
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

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }

    public List<Station> getStations() {
        return stations;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    public void setUpStationId(Long upStationId) {
        this.upStationId = upStationId;
    }

    public void setDownStationId(Long downStationId) {
        this.downStationId = downStationId;
    }

    public Section findSectionByUpStationId(Long upStationId) {
        return sections.stream()
                .filter(section -> section.getUpStationId().equals(upStationId))
                .findAny()
                .get();
    }

    public Section findSectionByDownStationId(Long downStationId) {
        return sections.stream()
                .filter(section -> section.getDownStationId().equals(downStationId))
                .findAny()
                .get();
    }

}
