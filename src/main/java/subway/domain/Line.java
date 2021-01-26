package subway.domain;

import java.util.List;

public class Line {
    private Long id;
    private final String name;
    private final String color;
    private Sections sections;


    public Line(Long id, String name, String color) {
        this.id = id;
        this.color = color;
        this.name = name;
    }

    public Line(Long id, String name, String color, Sections sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = sections;
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
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

    public void addSection(Section section) {
        sections.addSection(section);
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        sections.addSection(upStation, downStation, distance);
    }


    public void deleteSection(Station station) {
        sections.deleteSection(station);
    }

}
