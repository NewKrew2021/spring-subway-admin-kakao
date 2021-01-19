package subway.station;

import org.springframework.stereotype.Service;
import subway.exception.NotExistException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station createStation(Station station) {
        Station newStation = stationDao.save(station);
        return newStation;
    }

    public List<Station> getAllStations() {
        return stationDao.findAll().stream()
                .collect(Collectors.toList());
    }

    public Station getStation(long stationId) {
        Station station = stationDao.findById(stationId);
        if (station == null) {
            throw new NotExistException("해당 역이 존재하지 않습니다.");
        }
        return station;
    }

    public void deleteStation(long id) {
        stationDao.deleteById(id);
    }

    public boolean existName(String name) {
        return stationDao.countByName(name) != 0;
    }
}
