package subway.line;

import subway.station.StationResponse;

public class SectionResponse {
    private StationResponse station;
    private int distance;

    public SectionResponse() { }

    public SectionResponse(StationResponse station, int distance) {
        this.station = station;
        this.distance = distance;
    }

    public StationResponse getStation() {
        return station;
    }

    public int getDistance() {
        return distance;
    }
}
