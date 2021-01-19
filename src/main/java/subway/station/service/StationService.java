package subway.station.service;

import subway.station.vo.Station;
import subway.station.vo.Stations;

import java.util.List;

public interface StationService {
    Station create(Station station);

    Station findStationById(Long id);

    Stations findStationsByIds(List<Long> ids);

    Stations findAllStations();

    void update(Station station);

    void delete(Long id);
}
