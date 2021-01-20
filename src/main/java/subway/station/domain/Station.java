package subway.station.domain;

import org.apache.commons.lang3.StringUtils;
import subway.station.vo.StationResultValue;

public class Station {
    private static final long UNUSED_ID = 0L;
    private static final String UNUSED_NAME = "unused";
    private final Long id;
    private final String name;

    public Station(Long id) {
        this(id, UNUSED_NAME);
    }

    public Station(String name) {
        this(UNUSED_ID, name);
    }

    public Station(Long id, String name) {
        this.id = id;
        this.name = name;

        checkIsValidStation();
    }

    public StationResultValue toResultValue() {
        return new StationResultValue(id, name);
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
        return name.equals(station.name);
    }

    @Override
    public int hashCode() {
        return id.intValue();
    }

    private void checkIsValidStation() {
        if (isNegativeID()) {
            throw new IllegalArgumentException("Station ID cannot be negative");
        }

        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Station name cannot be null or blank characters");
        }
    }

    private boolean isNegativeID() {
        return id < 0;
    }
}

