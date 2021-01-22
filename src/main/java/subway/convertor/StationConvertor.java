package subway.convertor;

import subway.domain.Station;
import subway.dto.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

public class StationConvertor {
    public static StationResponse convertStation(Station station) {
        return new StationResponse(station.getId(), station.getName());
    }

    public static List<StationResponse> convertStations(List<Station> stations) {
        return stations.stream()
                .map(station -> new StationResponse(station.getId(), station.getName()))
                .collect(Collectors.toList());
    }
}
