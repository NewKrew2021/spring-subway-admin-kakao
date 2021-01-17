package subway.line.section;

public class Distance {
    private int distance;

    public Distance() {

    }

    public static int addDistance(Distance distance1, Distance distance2) {
        if (distance1.getDistance() == Integer.MAX_VALUE || distance2.getDistance() == Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return distance1.getDistance() + distance2.getDistance();
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int calculateDistance(int distance) {
        if (this.distance == Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return this.distance - distance;
    }

    public boolean validateDistance(int distance) {
        return this.distance > distance;
    }

    public int getDistance() {
        return distance;
    }
}
