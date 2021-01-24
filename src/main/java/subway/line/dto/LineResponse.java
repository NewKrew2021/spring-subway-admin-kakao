package subway.line.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import subway.line.vo.LineResultValue;
import subway.station.dto.StationResponse;

import java.util.List;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    @JsonCreator
    public LineResponse(Long id, String name, String color, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineResponse of(LineResultValue resultValue) {
        return new LineResponse(resultValue.getID(), resultValue.getName(),
                resultValue.getColor(), resultValue.getStations().allToResponses());
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
