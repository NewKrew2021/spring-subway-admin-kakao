package subway.section.vo;

public class SectionCreateValue {
    private final long lineID;
    private final long upStationID;
    private final long downStationID;
    private final int distanceDiff;

    public SectionCreateValue(long lineID, long upStationID, long downStationID, int distanceDiff) {
        this.lineID = lineID;
        this.upStationID = upStationID;
        this.downStationID = downStationID;
        this.distanceDiff = distanceDiff;
    }

    public long getLineID() {
        return lineID;
    }

    public long getUpStationID() {
        return upStationID;
    }

    public long getDownStationID() {
        return downStationID;
    }

    public int getDistanceDiff() {
        return distanceDiff;
    }
}
