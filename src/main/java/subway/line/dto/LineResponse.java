package subway.line.dto;

import subway.station.dto.StationResponse;

import java.beans.ConstructorProperties;
import java.util.List;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    // TODO: ConstructorProperties를 parameterNamesModule 로 바꿔보기?
    //       or 기본 생성자 생성 후 private?
    @ConstructorProperties({"id", "name", "color", "stations"})
    public LineResponse(Long id, String name, String color, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public Long getID() {
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
