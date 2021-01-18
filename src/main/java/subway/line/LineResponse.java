package subway.line;

import subway.station.StationResponse;

import java.util.List;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private int extraFare;
    private List<StationResponse> stations;

    public LineResponse() {
        // 없으면 jackson.databind 오류가 발생함. 기본생성자가 있어야 가능하다.
    }


    public LineResponse(Line line, List<StationResponse> stationResponses) {
        this.id = line.getId();
        this.name = line.getName();
        this.color = line.getColor();
        this.stations = stationResponses;
    }

    public LineResponse(Long id, String name, String color, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
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

    public List<StationResponse> getStations() {
        return stations;
    }
}
