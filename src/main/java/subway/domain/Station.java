package subway.domain;

import subway.dto.StationRequest;

public class Station {
    private Long id;
    private String name;

    public Station() {
    }

    public Station(StationRequest stationRequest) {
        this.name = stationRequest.getName();
    }

    public Station(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Station(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

