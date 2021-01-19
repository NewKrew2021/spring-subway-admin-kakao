package subway.station;

import org.springframework.stereotype.Service;
import subway.exceptions.DuplicateStationNameException;

import java.util.List;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station save(Station station) {
        validateDuplicateStationName(station.getName());
        return stationDao.save(station);
    }

    private void validateDuplicateStationName(String name) {
        if (stationDao.countByName(name) > 0) {
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
