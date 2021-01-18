package subway.station;

public class StationRequest {
    private String name;

    StationRequest() {
    }

    public StationRequest(String name) {
        this.name = name;
    }

    public Station toEntity() {
        return new Station(name);
    }

    public String getName() {
        return name;
    }
}
