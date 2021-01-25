package subway.controller.station;

import subway.domain.station.Station;

import java.util.Objects;

public class StationResponse {
    private Long id;
    private String name;

    public StationResponse() {
    }

    public StationResponse(Station station) {
        this.id = station.getId();
        this.name = station.getName();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StationResponse response = (StationResponse) o;
        return Objects.equals(id, response.id) && Objects.equals(name, response.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
