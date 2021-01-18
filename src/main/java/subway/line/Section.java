package subway.line;

import subway.station.Station;

import java.util.Objects;

public class Section {
    private Station station;
    private int distance;

    public Section(Station station, int distance) {
        this.station = station;
        this.distance = distance;
    }

    public void setDistance(int newDistance) {
        distance = newDistance;
    }

    public SectionResponse toDto() {
        return new SectionResponse(station.toDto(), distance);
    }

    public Station getStation() {
        return station;
    }

    public int getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(station, section.station);
    }

    @Override
    public int hashCode() {
        return Objects.hash(station);
    }
}
