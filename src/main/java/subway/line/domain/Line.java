package subway.line.domain;

import subway.section.domain.Section;
import subway.section.domain.Sections;

import java.util.List;

public class Line {
    private Long id;
    private String name;
    private String color;
    private Sections sections;

    private Line(String name, String color) {
        this(null, name, color);
    }

    private Line(Long id, String name, String color) {
        this(id, name, color, null);
    }

    private Line(Long id, String name, String color, List<Section> sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = Sections.of(sections);
    }

    public static Line of(LineRequest lineRequest) {
        return new Line(lineRequest.getName(), lineRequest.getColor());
    }

    public static Line of(Long id, String name, String color) {
        return new Line(id, name, color);
    }

    public static Line of(Long id, List<Section> sections) {
        return new Line(id, null, null, sections);
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

    public List<Long> getStationIds(Long id) {
        return sections.getStationIds();
    }

    public void checkAddSectionException(Section newSection) {
        sections.checkSameSection(newSection);
        sections.checkNoStation(newSection);
    }

    public boolean isEndPointSection(Section newSection) {
        return sections.isEndPointSection(newSection);
    }

    public Section sameUpSationId(Long stationId) {
        return sections.sameUpSationId(stationId);
    }

    public Section sameDownSationId(Long stationId) {
        return sections.sameDownSationId(stationId);
    }

    public void checkOneSection() {
        sections.checkOneSection();
    }

    public List<Section> getEndPointSections(Long stationId) {
        return sections.getEndPointSections(stationId);
    }
}
