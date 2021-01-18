package subway.line;

import subway.section.Section;

import java.util.ArrayList;
import java.util.List;

public class Line {
    private Long id;
    private String color;
    private String name;
    private Long upStationId;
    private Long downStationId;

    private List<Section> sections = new ArrayList<>();

    public Line() {
    }

    @Override
    public String toString() {
        return "Line{" +
                "id=" + id +
                ", color='" + color + '\'' +
                ", name='" + name + '\'' +
                ", upStationId=" + upStationId +
                ", downStationId=" + downStationId +
                ", sections=" + sections +
                '}';
    }

    public Line(String color, String name) {
        this.color = color;
        this.name = name;
    }

    public Line(Long id, String color, String name) {
        this.id = id;
        this.color = color;
        this.name = name;
    }

    public Line(String color, String name, Long upStationId, Long downStationId) {
        this.color = color;
        this.name = name;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
    }

    public Line(Long id, String color, String name, Long upStationId, Long downStationId) {
        this.id = id;
        this.color = color;
        this.name = name;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
    }

    public Long getId() {
        return id;
    }

    public String getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public List<Section> getSections() {
        return sections;
    }
}
