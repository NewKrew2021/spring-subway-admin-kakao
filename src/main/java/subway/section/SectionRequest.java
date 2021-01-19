package subway.section;

import subway.line.LineRequest;

public class SectionRequest {
    private long upStationId;
    private long downStationId;
    private int distance;

    public SectionRequest() {
    }

    public SectionRequest(long upStationId, long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public static SectionRequest of(LineRequest lineRequest) {
        return new SectionRequest(
                lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance()
        );
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
}
