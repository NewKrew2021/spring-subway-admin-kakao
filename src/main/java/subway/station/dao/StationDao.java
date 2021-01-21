package subway.station.dao;

import subway.station.entity.Station;
import subway.station.entity.Stations;

import java.util.List;
import java.util.Optional;

public interface StationDao {
    Station insert(Station station);

    Optional<Station> findStationById(Long id);

    Stations findStationsByIds(List<Long> ids);

    Stations findAllStations();

    int update(Station station);

    int delete(Long id);
}
