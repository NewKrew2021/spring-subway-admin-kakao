package subway.line;

import subway.station.Station;
import subway.station.StationResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private int extraFare;
    private List<StationResponse> stations;

    public LineResponse() {
    }

    public LineResponse(Long id, String name, String color, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public LineResponse(Line line) {
        this.id = line.getId();
        this.name = line.getName();
        this.color = line.getColor();
//        this.stations = line.getSections().stream()
//                .flatMap(section -> Arrays.asList(section.getUpStation(), section.getDownStation()))
//                .distinct()
//                .collect(Collectors.toList());

        this.stations = IntStream.range(1, line.getSections().size())
                .mapToObj(line.getSections()::get)
                .map(section -> new StationResponse(section.getUpStation()))
                .collect(Collectors.toList());
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
