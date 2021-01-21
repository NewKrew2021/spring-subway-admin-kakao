package subway.service;

import org.springframework.stereotype.Service;
import subway.domain.Station;
import subway.dao.StationDao;

import java.util.List;

@Service
public class StationService {
    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station createStation(Station station) {
        return stationDao.save(station);
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    public void deleteStationById(Long id) {
        stationDao.deleteById(id);
    }
}
