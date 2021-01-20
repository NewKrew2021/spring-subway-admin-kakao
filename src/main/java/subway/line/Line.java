package subway.line;
import java.util.List;
import java.util.stream.Collectors;
import java.util.*;
import subway.section.SectionDao;
import subway.station.StationDao;

public class Line {

    private Long id;
    private int extraFare;
    private String color;
    private String name;

    public Line() {
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

    public int getExtraFare() { return extraFare;}

    public List<Long> getStationInfo(StationDao stationDao, SectionDao sectionDao) {
        List<Long> stations = new ArrayList<>();

        sectionDao
                .findByLineId(id).stream()
                .forEach(section -> {
                    stations.add(stationDao.findById(section.getUpStationId()).getId());
                    stations.add(stationDao.findById(section.getDownStationId()).getId());
                });
        return stations.stream().distinct().collect(Collectors.toList());
    }

    public Long getUpStationId(SectionDao sectionDao) {
        return sectionDao.getUpStationId(id);
    }

    public Long getDownStationId(SectionDao sectionDao) {
        return sectionDao.getDownStationId(id);
    }
}
