package subway.line.dto;

import subway.line.vo.Line;
import subway.section.vo.Section;
import subway.station.dto.StationResponse;
import subway.station.vo.Stations;

import java.util.List;
import java.util.stream.Collectors;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    //    private int extraFare;
    private List<StationResponse> stationResponses;

    public LineResponse() {
    }

    public LineResponse(Long id, String name, String color, List<StationResponse> stationResponses) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stationResponses = stationResponses;
    }

    public LineResponse(Line line, Stations stations) {
        this(line.getId(),
                line.getName(),
                line.getColor(),
                stations.stream()
                        .map(StationResponse::new)
                        .collect(Collectors.toList())
        );
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

    public List<StationResponse> getStationResponses() {
        return stationResponses;
    }
}
