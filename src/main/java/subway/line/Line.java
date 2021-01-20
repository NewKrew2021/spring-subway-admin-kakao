package subway.line;

import subway.exception.NoContentException;
import subway.exception.TwoStationException;
import subway.station.Station;

import java.util.*;
import java.util.stream.IntStream;

public class Line {
    private Long id;
    private String name;
    private String color;
    private final Sections sections;

    public Line() {
        this.sections = new Sections();
    }

    public Line(String name, String color) {
        this();
        this.name = name;
        this.color = color;
    }

    public Line(Long id, String name, String color) {
        this(name, color);
        this.id = id;
    }

    public Line(Long id, String name, String color, List<Section> sections) {
        this(id, name, color);
        this.sections.initSections(sections);
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

    public Sections getSections() {
        return sections;
    }


    @Override
    public String toString() {
        return "Line{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", sections=" + sections +
                '}';
    }

}
