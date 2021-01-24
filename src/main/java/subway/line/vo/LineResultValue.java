package subway.line.vo;

import subway.line.domain.Line;
import subway.line.dto.LineResponse;
import subway.station.vo.StationResultValues;

public class LineResultValue {
    private final long id;
    private final String name;
    private final String color;
    private final StationResultValues stations;

    public LineResultValue(long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = new StationResultValues();
    }

    public LineResultValue(long id, String name, String color, StationResultValues stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineResultValue of(Line line, StationResultValues stations) {
        return new LineResultValue(line.getID(), line.getName(), line.getColor(), stations);
    }

    public LineResponse toLineResponse() {
        return new LineResponse(id, name, color, stations.allToResponses());
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

    public StationResultValues getStations() {
        return stations;
    }
}
