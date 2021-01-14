package subway.line;

import org.springframework.util.ReflectionUtils;
import subway.station.Station;
import subway.station.StationResponse;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Line {
    private Long id;
    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private int distance;
    private List<Station> stations;

    public Line(String name, String color, Long upStationId, Long downStationId, int distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
        this.stations=stations=new ArrayList<>();
    }

    public Line(Long id,String name, String color, Long upStationId, Long downStationId, int distance) {
        this.id=id;
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
        this.stations=stations=new ArrayList<>();

    }

    public Line(LineRequest lineRequest){
        this.name=lineRequest.getName();
        this.color=lineRequest.getColor();
        this.upStationId=lineRequest.getUpStationId();
        this.downStationId=lineRequest.getDownStationId();
        this.distance=lineRequest.getDistance();
        this.stations=stations=new ArrayList<>();

    }

    public void modify(LineRequest lineRequest){
        this.name=lineRequest.getName();
        this.color=lineRequest.getColor();
        this.upStationId=lineRequest.getUpStationId();
        this.downStationId=lineRequest.getDownStationId();
        this.distance=lineRequest.getDistance();
    }

    public void add(Station station){
        stations.add(station);
    }

    public long getId(){
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
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

    public List<Station> getStations() {
        return stations;
    }
}
