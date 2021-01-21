package subway.domain.station;

public class Station {
    private Long id;
    private String name;

    public Station() {
    }

    private Station(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    private Station(String name) {
        this.name = name;
    }

    public static Station of(String name) {
        return new Station(name);
    }

    public static Station of(Long id, String name) {
        return new Station(id, name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

