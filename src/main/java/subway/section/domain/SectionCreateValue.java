package subway.section.domain;

public class SectionCreateValue {

    private final long lineId;
    private final long upStationId;
    private final long downStationId;
    private final int distance;

    public SectionCreateValue(long lineId, long upStationId, long downStationId, int distance) {
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public long getLineId() {
        return lineId;
    }

    public long getUpStationId() {
        return upStationId;
    }

    public long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }

    public static class Pending {
        private final long upStationId;
        private final long downStationId;
        private final int distance;

        public Pending(long upStationId, long downStationId, int distance) {
            this.upStationId = upStationId;
            this.downStationId = downStationId;
            this.distance = distance;
        }

        public SectionCreateValue toCreateValue(long lineId) {
            return new SectionCreateValue(lineId, upStationId, downStationId, distance);
        }
    }
}
