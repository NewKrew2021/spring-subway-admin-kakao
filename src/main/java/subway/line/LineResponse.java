package subway.line;

import subway.station.Station;

import java.util.List;
import java.util.stream.Collectors;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private int extraFare;
    private List<Station> stations;

    public LineResponse() {
    }

    public LineResponse(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public LineResponse(Line line, List<Station> stations) {
        this(line.getId(), line.getName(), line.getColor());
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

    public List<Station> getStations() {
        return stations;
    }

    private boolean validator(Line line) {
        if (line == null) {
            return false;
        }
        return true;
    }

    public static List<LineResponse> getLineResponses(List<Line> lines) {
        return lines.stream()
                .map(line -> new LineResponse(line.getId(),
                        line.getName(),
                        line.getColor()))
                .collect(Collectors.toList());
    }
}
