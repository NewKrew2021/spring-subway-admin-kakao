package subway.station;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class Station {
    private final Long id;
    private final String name;

    public Station(String name) {
        this(0L, name);
    }

    public Station(Long id, String name) {
        if (isNegative(id)) {
            throw new IllegalArgumentException("Station ID cannot be negative");
        }

        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Station name cannot be null or blank characters");
        }

        this.id = id;
        this.name = name;
    }

    public StationResponse toDto() {
        return new StationResponse(id, name);
    }

    public Long getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Station station = (Station) o;
        return Objects.equals(name, station.name);
    }

    @Override
    public int hashCode() {
        return id.intValue();
    }

    private boolean isNegative(Long id) {
        return id < 0;
    }
}

