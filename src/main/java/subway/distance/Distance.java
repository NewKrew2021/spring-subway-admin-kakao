package subway.distance;

public class Distance {

    public static final int VIRTUAL_DISTANCE = 0;
    private final int distance;

    public Distance() {
        this.distance = VIRTUAL_DISTANCE;
    }

    public Distance(int distance) {
        this.distance = distance;
    }

    public Distance sumDistance(Distance another) {
        return Math.min(this.distance, another.distance) == VIRTUAL_DISTANCE
                ? new Distance()
                : new Distance(this.distance + another.distance);
    }

    public Distance subtractDistance(int another) {
        return new Distance(this.distance - another);
    }

    public boolean isExist(Distance another) {
        return distance != VIRTUAL_DISTANCE && distance <= another.distance;
    }

    public int getDistance() {
        return distance;
    }

}
