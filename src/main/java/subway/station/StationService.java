package subway.station;

import org.springframework.stereotype.Service;
import subway.exception.exceptions.DuplicateStationNameException;

import java.util.List;

@Service
public class StationService {

    private static final int MIN_DUPLICATE_STATION_NAME_COUNT = 1;
    private static final int MUST_DELETE_COUNT = 1;

    private static final String DUPLICATE_STATION_NAME_MESSAGE = "중복된 역 이름입니다.";

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station save(StationRequest stationRequest) {
        validateDuplicateStationName(stationRequest.getName());
        return stationDao.save(stationRequest.toStation());
    }

    private void validateDuplicateStationName(String name) {
        if (stationDao.countByName(name) >= MIN_DUPLICATE_STATION_NAME_COUNT) {
            throw new DuplicateStationNameException(DUPLICATE_STATION_NAME_MESSAGE);
        }
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    public boolean deleteById(long id) {
        if(stationDao.deleteById(id) == MUST_DELETE_COUNT) {
            return true;
        }
        return false;
    }
}
