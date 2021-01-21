package subway.station;

public class Station {
    private Long id;
    private String name;

    public Station(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Station(String name) {
        this(0L, name);
    }

    public Station(StationRequest stationRequest) {
        this(stationRequest.getName());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
