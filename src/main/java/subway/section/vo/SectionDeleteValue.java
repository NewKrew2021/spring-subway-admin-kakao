package subway.section.vo;

public class SectionDeleteValue {
    private final long lineID;
    private final long stationID;

    public SectionDeleteValue(long lineID, long stationID) {
        this.lineID = lineID;
        this.stationID = stationID;
    }

    public long getLineID() {
        return lineID;
    }

    public long getStationID() {
        return stationID;
    }
}
