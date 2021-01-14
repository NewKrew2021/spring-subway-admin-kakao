package subway.line;

import subway.station.Station;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Line {
    private Long id;
    private String color;
    private String name;
    private List<Section> sections = new ArrayList<>();

    public Line() {
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

    public Line(String color, String name, Long upStationId, Long downStationId, Integer distance) {
        this.color = color;
        this.name = name;
        sections.add(new Section(upStationId, downStationId, distance));

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

    public List<Section> getSections() {
        return sections;
    }
}
