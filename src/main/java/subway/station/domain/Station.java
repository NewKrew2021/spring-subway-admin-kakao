package subway.station.domain;

import org.apache.commons.lang3.StringUtils;
import subway.station.dto.StationResponse;

import java.util.Objects;

public class Station {
    private final Long id;
    private final String name;

    private static final Long MINIMUM_ID = 0L;

    public Station(Long id, String name) {
        validateId(id);
        validateName(name);

        this.id = id;
        this.name = name;
    }

    private void validateId(Long id) {
        if (id < MINIMUM_ID) {
            throw new IllegalArgumentException("Station ID cannot be negative");
        }
    }

    private void validateName(String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Station name cannot be null or blank characters.");
        }
    }

    public StationResponse toDto() {
        return new StationResponse(id, name);
    }

    public Long getId() {
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
}

