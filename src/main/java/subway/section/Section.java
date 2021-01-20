package subway.section;

import java.util.Objects;

public class Section implements Comparable<Section> {
    private final long id;
    private final long lineId;
    private final long stationId;
    private final int distance;

    public Section(long lineId, long stationId, int distance) {
        this(0L, lineId, stationId, distance);
    }

    public Section(long id, long lineId, long stationId, int distance) {
        this.id = id;
        this.lineId = lineId;
        this.stationId = stationId;
        this.distance = distance;
    }

    public long getId() {
        return id;
    }

    public long getLineId() {
        return lineId;
    }

    public long getStationId() {
        return stationId;
    }

    public int getDistance() {
        return distance;
    }

    public int distanceDiffWith(Section existingSection) {
        return distance - existingSection.distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(lineId, section.lineId) && Objects.equals(stationId, section.stationId);
    }

    @Override
    public int hashCode() {
        return (int) id;
    }

    @Override
    public int compareTo(Section that) {
        return distance - that.getDistance();
    }
}
