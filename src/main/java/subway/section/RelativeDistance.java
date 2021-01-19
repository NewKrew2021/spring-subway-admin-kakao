package subway.section;

public class RelativeDistance {
    private final int relativeDistance;

    public RelativeDistance(int relativeDistance) {
        this.relativeDistance = relativeDistance;
    }

    public int calculateUpStationRelativeDistance(int distance) {
        return relativeDistance - distance;
    }

    public int calculateDownStationRelativeDistance(int distance) {
        return relativeDistance + distance;
    }

    public int calculateDistanceDifference(RelativeDistance otherRelativeDistance) {
        return relativeDistance - otherRelativeDistance.relativeDistance;
    }

    public int getRelativeDistance() {
        return relativeDistance;
    }

}
