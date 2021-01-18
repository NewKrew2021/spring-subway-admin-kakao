package subway.line;
import subway.section.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.*;import subway.section.SectionDao;
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

    public void updateAll(LineRequest line){
        this.name = line.getName();
        this.color = line.getColor();
        this.extraFare = line.getExtraFare();
    }

    public int getExtraFare() { return this.extraFare; }

    public String getColor() {
        return color;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Long> getStationInfo(StationDao stationDao, SectionDao sectionDao) {
//        Set<Long> stations = new HashSet<>();
//        SectionDao.getInstance()
//                .findAll().stream()
//                .filter(section -> section.getLineId() == id)
//                .forEach(section -> {
//                    stations.add(StationDao.getInstance().findById(section.getUpStationId()).getId());
//                    stations.add(StationDao.getInstance().findById(section.getDownStationId()).getId());
//                });
//        return new ArrayList<>(stations);

        List<Long> stations = new ArrayList<>();

        sectionDao
                .findByLineId(id).stream()
                .forEach(section -> {
                    stations.add(stationDao.findById(section.getUpStationId()).getId());
                    stations.add(stationDao.findById(section.getDownStationId()).getId());
                });
        return stations.stream().distinct().collect(Collectors.toList());
//        List<Section> sections = new ArrayList<>();
//        sections.add(SectionDao.getInstance()
//                .findAll().stream()
//                .filter(section -> section.getLineId() == id)
//                .collect(Collectors.toList()).get(0));
//        alignStations(sections);
//        List<Long> stations_ = new ArrayList<>();
//        stations_.add(sections.get(0).getUpStationId());
//        stations_.add(sections.get(0).getDownStationId());
//        for (int i = 1; i < sections.size(); i++) {
//            stations_.add(sections.get(i).getDownStationId());
//        }
//        return stations_;
    }

//    public void alignStations(List<Section> sections_){
//        // 0번째의 상행id 가 하행id인놈을 찾는다.
//        // 찾아지면 맨앞에다가 추가하고
//        if(SectionDao.getInstance().ifDownIdExist(sections_.get(0).getUpStationId()) != -1){
//            sections_.add(0, SectionDao.getInstance().findById(SectionDao.getInstance().ifDownIdExist(sections_.get(0).getUpStationId())));
//            alignStations(sections_);
//        }
//
//        // 가장 마지막의 하행id를 찾는다
//        // 그 id가 상행 id인놈을 찾는다
//        // 찾아지면 맨뒤에다가 추가하고
//        if(SectionDao.getInstance().ifUpIdExist(sections_.get(sections_.size()-1).getDownStationId()) != -1){
//            sections_.add(0, SectionDao.getInstance().findById(SectionDao.getInstance().ifUpIdExist(sections_.get(sections_.size()-1).getDownStationId())));
//            alignStations(sections_);
//        }
//    }

    public Long getUpStationId(SectionDao sectionDao) {
        return sectionDao.getUpStationId(id);
    }

    public Long getDownStationId(SectionDao sectionDao) {
        return sectionDao.getDownStationId(id);
    }

    public int getDistance(SectionDao sectionDao) {
        return sectionDao.getDistance(id);
    }
}
