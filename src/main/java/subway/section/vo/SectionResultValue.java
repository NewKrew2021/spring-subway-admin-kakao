package subway.section.vo;

import subway.station.dto.StationResponse;

public class SectionResultValue {
    private final long lineID;
    private final long stationID;
    private final int distance;

    public SectionResultValue(long lineID, long stationID, int distance) {
        this.lineID = lineID;
        this.stationID = stationID;
        this.distance = distance;
    }

    public StationResponse toStationResponse() {
        return new StationResponse(stationID, "asdf");
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
}
