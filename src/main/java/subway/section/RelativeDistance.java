package subway.section;

public class RelativeDistance {
    private final int relativeDistance;

    public RelativeDistance(int relativeDistance) {
        this.relativeDistance = relativeDistance;
    }

    static int upStationDistance(int distance) {
        return -distance;
    }

    static int downStationDistance(int distance) {
        return distance;
    }

    public int calculateRelativeDistance(int distance) {
        return relativeDistance + distance;
    }

    public int calculateDistanceDifference(RelativeDistance otherRelativeDistance) {
        return relativeDistance - otherRelativeDistance.relativeDistance;
    }

    public boolean isBetween(int upStationRelativeDistance, int downStationRelativeDistance) {
        return relativeDistance >= upStationRelativeDistance && relativeDistance <= downStationRelativeDistance;
    }

    public int getRelativeDistance() {
        return relativeDistance;
    }

}
