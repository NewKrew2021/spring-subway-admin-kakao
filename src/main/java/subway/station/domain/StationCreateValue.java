package subway.station.domain;

public class StationCreateValue {

    private final String name;

    public StationCreateValue(String name) {
        this.name = name;
    }

    public Station toEntity() {
        return new Station(name);
    }

    public String getName() {
        return name;
    }
}
