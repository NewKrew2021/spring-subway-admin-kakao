package subway.factory;

import subway.domain.Station;
import subway.dto.StationRequest;

public class StationFactory {
    public static Station getStation(StationRequest stationRequest) {
        return new Station(stationRequest.getName());
    }
}
