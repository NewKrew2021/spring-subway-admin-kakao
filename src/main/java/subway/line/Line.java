package subway.line;

import subway.section.Section;
import subway.section.Sections;

import java.util.List;

public class Line {

    private long id;
    private final String name;
    private final String color;
    private Sections sections;

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(long id, String name, String color, Sections sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = sections;
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

    public Sections getSections() {
        return sections;
    }

    public List<Long> getStationIds() {
        return sections.getStationIds();
    }

    public void addSection(Section newSection) {
        sections.addSection(newSection);
    }

    public void deleteSection(long stationId) {
        sections.deleteSection(stationId);
    }
}
