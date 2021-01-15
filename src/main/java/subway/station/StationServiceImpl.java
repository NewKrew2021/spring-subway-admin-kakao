package subway.station;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationServiceImpl implements StationService {
    private StationDao stationDao;

    public StationServiceImpl(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station save(Station station) {
        Station newStation = new Station(station.getName());
        return stationDao.save(newStation);
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    public Station findOne(Long stationId) {
        return stationDao.findOne(stationId);
    }

    public boolean deleteById(Long stationId) {
        return stationDao.deleteById(stationId) != 0;
    }

}
