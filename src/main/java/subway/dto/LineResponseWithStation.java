package subway.dto;

import java.util.List;

public class LineResponseWithStation extends LineResponse{
    private final List<StationResponse> stations;

    public LineResponseWithStation(Long id, String name, String color, List<StationResponse> stations) {
        super(id, name, color);
        this.stations = stations;
    }

    public List<StationResponse> getStations() {
        return stations;
    }
}
