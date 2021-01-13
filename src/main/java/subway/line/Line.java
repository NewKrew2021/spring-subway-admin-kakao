package subway.line;

import subway.station.Station;
import subway.station.StationResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Line {
    private Long id;
    private String name;
    private String color;
    private List<Station> stations;

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<StationResponse> getStations() {
        return Collections.unmodifiableList(new ArrayList<>());
    }
}
