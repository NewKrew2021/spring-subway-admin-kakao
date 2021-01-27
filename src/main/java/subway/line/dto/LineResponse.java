package subway.line.dto;

import subway.line.domain.Line;
import subway.station.dto.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

public class LineResponse {

    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;
    private int extraFare;

    public void setId(Long id) {
        this.id = id;
    }

    public LineResponse() {
    }

    public LineResponse(Long id, String name, String color, List<StationResponse> stations, int extraFare) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
        this.extraFare = extraFare;
    }

    public static LineResponse of(Line line){
        return new LineResponse(
                line.getId(),
                line.getName(),
                line.getColor(),
                line.getStations().stream()
                        .map(StationResponse::of)
                        .collect(Collectors.toList()),
                line.getExtraFare()
        );
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStations(List<StationResponse> stations) {
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

    public int getExtraFare() {
        return extraFare;
    }
}