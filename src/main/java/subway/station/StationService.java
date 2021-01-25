package subway.station;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station save(Station station) {
        return stationDao.save(station);
    }

    public void deleteById(Long id) {
        stationDao.deleteById(id);
    }

    public List<Station> getStationResponses() {
        return stationDao.findAll();
    }
}
