package subway.line.domain;

import subway.section.domain.Section;
import subway.section.repository.SectionDao;
import subway.station.domain.Station;

import java.util.List;

public class Line {

    private Long id;
    private int extraFare;
    private String color;
    private String name;
    private List<Section> sections;
    private List<Station> stations;

    public Line() {
    }

    public Line(Long id, String name, String color, int extraFare, List<Section> sections, List<Station> stations) {
        this(name, color, extraFare);
        this.id = id;
        this.sections = sections;
        this.stations = stations;
    }

    public Line(Long id, String name, String color, int extraFare) {
        this(name, color, extraFare);
        this.id = id;
    }

    public Line(String name, String color, int extraFare) {
        this.name = name;
        this.color = color;
        this.extraFare = extraFare;
    }

    public String getColor() {
        return color;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getExtraFare() {
        return extraFare;
    }

    public Long getUpStationId(SectionDao sectionDao) {
        return sectionDao.getUpStationId(id);
    }

    public Long getDownStationId(SectionDao sectionDao) {
        return sectionDao.getDownStationId(id);
    }

    public List<Section> getSections() {
        return sections;
    }

    public List<Station> getStations() {
        return stations;
    }
}
