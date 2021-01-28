package subway.station;

public class StationRequest {
    private String name;

    public StationRequest() {
    }

    public StationRequest(String name) {
        this.name = name;
    }

    public Station toDomainObject() {
        return Station.of(name);
    }

    public String getName() {
        return name;
    }
}
