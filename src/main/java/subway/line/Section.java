package subway.line;

import java.util.Objects;

public class Section implements Comparable<Section> {
    private final long id;
    private final long lineID;
    private final long stationID;
    private final int distance;

    public Section(long lineID, long stationID, int distance) {
        this(0L, lineID, stationID, distance);
    }

    public Section(long id, long lineID, long stationID, int distance) {
        this.id = id;
        this.lineID = lineID;
        this.stationID = stationID;
        this.distance = distance;
    }

    public long getID() {
        return id;
    }

    public long getLineID() {
        return lineID;
    }

    public long getStationID() {
        return stationID;
    }

    public int getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(lineID, section.lineID) && Objects.equals(stationID, section.stationID);
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
