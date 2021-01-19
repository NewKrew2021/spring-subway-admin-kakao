package subway.line;

import org.springframework.beans.factory.annotation.Autowired;
import subway.station.Station;
import subway.station.StationDao;

import java.util.*;
import java.util.stream.Collectors;

public class Line {
    private Long id;
    private String name;
    private String color;

//    @Autowired
//    private SectionService sectionService;
//
//    @Autowired
//    private StationDao stationDao;

    public Line(String name, String color) {
        this(0L, name, color);
    }

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(id, line.id) && Objects.equals(name, line.name) && Objects.equals(color, line.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color);
    }

//    public List<Long> getStationIds(){
//        List<Section> sections = sectionService.showAll(this.id);
//        List<Long> stationIds = sections.stream()å
//                .map(Section::getUpStationId)
//                .collect(Collectors.toList());
//        stationIds.add(sections.get(sections.size()-1).getDownStationId());
//        return stationIds;
//    }

}
