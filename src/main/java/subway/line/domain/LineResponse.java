package subway.line.domain;

import subway.station.domain.Station;

import java.util.List;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private int extraFare;
    private List<Station> stations;

    public LineResponse(){}

    public LineResponse(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public LineResponse(Long id, String name, String color, List<Station> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public LineResponse(Line line) {
        this.id = line.getId();
        this.name = line.getName();
        this.color = line.getColor();
    }


    public LineResponse(Line line, List<Station> stations) {
        if(!validator(line)){
            throw new IllegalArgumentException("노선 정보가 존재하지 않습니다.");
        }
        this.id = line.getId();
        this.name = line.getName();
        this.color = line.getColor();
        this.stations = stations;
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

    public List<Station> getStations() {
        return stations;
    }

    private boolean validator(Line line){
        if (line == null) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "LineResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", stations=" + stations +
                '}';
    }
}
