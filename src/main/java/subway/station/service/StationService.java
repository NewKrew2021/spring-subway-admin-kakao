package subway.station.service;

import subway.station.entity.Station;
import subway.station.entity.Stations;

import java.util.List;

public interface StationService {
    Station create(String name);

    Station getStationById(Long id);

    Stations getStationsByIds(List<Long> ids);

    Stations getAllStations();

    void update(Station station);

    void delete(Long id);
}
