package subway.section;

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

    public int distanceDiff(Section downSection) {
        return distance - downSection.distance;
    }

    public boolean isUpperThan(Section newSection) {
        return distance < newSection.distance;
    }

    public boolean isCloserFromThan(Section fromSection, Section thanSection) {
        return Math.abs(distance - fromSection.distance) < Math.abs(distance - thanSection.distance);
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
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Section that = (Section) o;
        return Objects.equals(lineID, that.lineID) && Objects.equals(stationID, that.stationID);
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
