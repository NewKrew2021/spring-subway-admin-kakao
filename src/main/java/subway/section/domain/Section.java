package subway.section.domain;

import java.util.Objects;

public class Section implements Comparable<Section> {
    private final long lineID;
    private final long stationID;
    private final int distance;

    public Section(long lineID, long stationID, int distance) {
        this.lineID = lineID;
        this.stationID = stationID;
        this.distance = distance;

        checkIsValidSection();
    }

    public int distanceDiff(Section downSection) {
        return Math.abs(distance - downSection.distance);
    }

    public boolean isUpperThan(Section newSection) {
        return distance < newSection.distance;
    }

    public boolean isFartherOrEqualFromThan(Section fromSection, Section thanSection) {
        return Math.abs(distance - fromSection.distance) >= Math.abs(distance - thanSection.distance);
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
        return Objects.hash(lineID, stationID);
    }

    @Override
    public int compareTo(Section that) {
        return distance - that.getDistance();
    }

    private void checkIsValidSection() {
        if (isNegativeID(lineID) || isNegativeID(stationID)) {
            throw new IllegalArgumentException("Line id and station id cannot be negative");
        }
    }

    private boolean isNegativeID(long id) {
        return id < 0;
    }
}
