package subway.station;

public class Station {
    private Long id;
    private String name;

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

