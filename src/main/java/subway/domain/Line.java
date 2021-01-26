package subway.domain;

import java.util.List;

public class Line {
    private Long id;
    private final String name;
    private final String color;
    private Sections sections;

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
        sections = new Sections();
    }

    public Line(String name, String color, Sections sections) {
        this(name, color);
        this.sections = sections;
    }

    public Line(Long id, String name, String color) {
        this(name, color);
        this.id = id;
        sections = new Sections();
    }

    public Line(Long id, String name, String color, Sections sections) {
        this(name, color, sections);
        this.id = id;
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

    public Station getUpStation() {
        return sections.getStartStation();
    }

    public Station getDownStationId() {
        return sections.getEndStation();
    }

    public Sections getSections() {
        return sections;
    }

    public List<Station> getStations() {
        return sections.getStations();
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        sections.addSection(new Section(upStation, downStation, distance, id));
    }

    public void deleteSection(Station station) {
        sections.deleteSection(station);
    }

}
