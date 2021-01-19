package subway.station;

import org.springframework.stereotype.Service;
import subway.exception.exceptions.DuplicateStationNameException;

import java.util.List;

@Service
public class StationService {

    private static final int MIN_DUPLICATE_STATION_NAME_COUNT = 1;

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station save(Station station) {
        validateDuplicateStationName(station.getName());
        return stationDao.save(station);
    }

    private void validateDuplicateStationName(String name) {
        if (stationDao.countByName(name) >= MIN_DUPLICATE_STATION_NAME_COUNT) {
            throw new DuplicateStationNameException("중복된 역 이름입니다.");
        }
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    public boolean deleteById(long id) {
        if(stationDao.deleteById(id) == 1) {
            return true;
        }
        return false;
    }
}
