package subway.line;

import subway.station.StationDao;
import subway.station.StationResponse;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;
    private int extraFare;

    public LineResponse(){ }

    public LineResponse(Long id, String name, String color, List<StationResponse> stations, int extraFare) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
        this.extraFare = extraFare;
    }

    public LineResponse(Line line){
        this.id = line.getId();
        this.name = line.getName();
        this.color = line.getColor();
        this.extraFare = line.getExtraFare();
        this.stations = Arrays.asList(line.getUpStationId(), line.getDownStationId()).stream()
                .map(val -> new StationResponse(val, StationDao.getInstance().findById(val).getName())).collect(Collectors.toList());
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

    public int getExtraFare() { return extraFare;}

    public List<StationResponse> getStations() {
        return stations;
    }
}
