package subway.line;

import subway.station.StationResponse;

import java.util.List;

public class LineResponse {

    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;
    private int extraFare;

    public void setId(Long id){
        this.id = id;
    }

    public LineResponse(){ }

    public LineResponse(Long id, String name, String color, List<StationResponse> stations, int extraFare) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
        this.extraFare = extraFare;
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
