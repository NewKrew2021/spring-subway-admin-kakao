package subway.line.vo;

import subway.line.dto.LineResponse;
import subway.station.dto.StationResponse;

import java.util.List;

public class LineResultValue {
    private final long id;
    private final String name;
    private final String color;

    public LineResultValue(long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public LineResponse toLineResponse(List<StationResponse> stationResponses) {
        return new LineResponse(id, name, color, stationResponses);
    }

    public long getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}
