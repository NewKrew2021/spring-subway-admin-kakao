package subway.section.vo;

import subway.line.dto.LineRequest;
import subway.section.dto.SectionRequest;

public class SectionCreateValue {
    private final long lineID;
    private final long upStationID;
    private final long downStationID;
    private final int distanceDiff;

    public SectionCreateValue(long lineID, LineRequest lineRequest) {
        this.lineID = lineID;
        this.upStationID = lineRequest.getUpStationID();
        this.downStationID = lineRequest.getDownStationID();
        this.distanceDiff = lineRequest.getDistanceDiff();
    }

    public SectionCreateValue(long lineID, SectionRequest sectionRequest) {
        this.lineID = lineID;
        this.upStationID = sectionRequest.getUpStationID();
        this.downStationID = sectionRequest.getDownStationID();
        this.distanceDiff = sectionRequest.getDistanceDiff();
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
