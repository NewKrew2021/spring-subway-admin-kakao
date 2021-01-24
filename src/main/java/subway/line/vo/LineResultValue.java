package subway.line.vo;

import subway.line.domain.Line;
import subway.station.domain.Stations;

public class LineResultValue {
    private final long id;
    private final String name;
    private final String color;
    private final Stations stations;

    public LineResultValue(long id, String name, String color, Stations stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineResultValue of(Line line, Stations stations) {
        return new LineResultValue(line.getID(), line.getName(), line.getColor(), stations);
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

    public Stations getStations() {
        return stations;
    }
}
