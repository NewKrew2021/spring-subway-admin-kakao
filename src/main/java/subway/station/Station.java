package subway.station;

public class Station {
    private Long id;
    private String name;

    public Station(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Station(String name) {
        this(null, name);
    }

    public static Station fromRequest(StationRequest stationRequest) {
        return new Station(stationRequest.getName());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public StationResponse toResponse() {
        return new StationResponse(getId(), getName());
    }
}
