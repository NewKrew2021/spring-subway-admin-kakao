package subway.line;

import subway.section.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import subway.station.*;
import java.util.*;import subway.section.SectionDao;
import subway.station.StationDao;

import java.util.*;import subway.section.SectionDao;
import subway.station.StationDao;

import java.util.*;import subway.section.SectionDao;
import subway.station.StationDao;

import java.util.*;import subway.section.SectionDao;
import subway.station.StationDao;

import java.util.*;import subway.section.SectionDao;
import subway.station.StationDao;

import java.util.*;import subway.section.SectionDao;
import subway.station.StationDao;

import java.util.*;

public class Line {
    private Long id;
    private int extraFare;
    private String color;
    private String name;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public Line() {
    }

    public Line(String name, String color, Long upStationId, Long downStationId, int distance, int extraFare) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
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

    public List<Long> getStationInfo() {
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
        SectionDao.getInstance()
                .findAll().stream()
                .filter(section -> section.getLineId() == id)
                .forEach(section -> {
                    stations.add(StationDao.getInstance().findById(section.getUpStationId()).getId());
                    stations.add(StationDao.getInstance().findById(section.getDownStationId()).getId());
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

    public void alignStations(List<Section> sections_){
        // 0번째의 상행id 가 하행id인놈을 찾는다.
        // 찾아지면 맨앞에다가 추가하고
        if(SectionDao.getInstance().ifDownIdExist(sections_.get(0).getUpStationId()) != -1){
            sections_.add(0, SectionDao.getInstance().findById(SectionDao.getInstance().ifDownIdExist(sections_.get(0).getUpStationId())));
            alignStations(sections_);
        }

        // 가장 마지막의 하행id를 찾는다
        // 그 id가 상행 id인놈을 찾는다
        // 찾아지면 맨뒤에다가 추가하고
        if(SectionDao.getInstance().ifUpIdExist(sections_.get(sections_.size()-1).getDownStationId()) != -1){
            sections_.add(0, SectionDao.getInstance().findById(SectionDao.getInstance().ifUpIdExist(sections_.get(sections_.size()-1).getDownStationId())));
            alignStations(sections_);
        }
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return distance == line.distance &&
                Objects.equals(color, line.color) &&
                Objects.equals(name, line.name) &&
                Objects.equals(upStationId, line.upStationId) &&
                Objects.equals(downStationId, line.downStationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, name, upStationId, downStationId, distance);
    }
}
