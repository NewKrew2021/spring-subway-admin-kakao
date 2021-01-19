package subway.station.vo;

public class Station {
    private Long id;
    private final String name;

    public Station(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Station(String name) {
        this.name = name;
    }

    public Station(Long id, Station station) {
        this(id, station.getName());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
