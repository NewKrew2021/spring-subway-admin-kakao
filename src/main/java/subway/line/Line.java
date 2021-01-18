package subway.line;

import subway.section.Section;

import java.util.ArrayList;
import java.util.List;

public class Line {
    private Long id;
    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;

    private List<Section> sections = new ArrayList<>();

    public Line() {
    }

    public Line(String name, String color) {
        this.color = color;
        this.name = name;
    }

    public Line(Long id, String name, String color) {
        this.id = id;
        this.color = color;
        this.name = name;
    }

    public Line(String name, String color, Long upStationId, Long downStationId) {
        this.color = color;
        this.name = name;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
    }

    public Line(Long id, String name, String color, Long upStationId, Long downStationId) {
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
