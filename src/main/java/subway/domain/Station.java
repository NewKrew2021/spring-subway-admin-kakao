package subway.domain;

import java.util.Objects;

public class Station {
    public static final int ZERO = 0;
    private Long id;
    private String name;

    public Station() {
    }

    public Station(Long id, String name) {
        if (id < ZERO) {
            throw new IllegalStationException();
        }
        this.id = id;
        this.name = name;
    }

    public Station(String name) {
        this.name = name;
    }

    public Station(Long id) {
        if (id < ZERO) {
            throw new IllegalStationException();
        }
        this.id = id;
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
        Station station = (Station) o;
        return Objects.equals(id, station.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

