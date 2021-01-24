package subway.station;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import subway.station.domain.Station;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class StationService {
    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station createWithName(String stationName) {
        return insert(stationName);
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    public Station findByID(long stationID) {
        return findOneBy(stationID);
    }

    public void deleteByID(long stationID) {
        stationDao.deleteByID(stationID);
    }

    private Station insert(String stationName) {
        try {
            return stationDao.insert(new Station(stationName));
        } catch (DataAccessException e) {
            throw new IllegalArgumentException(
                    String.format("%s\nStation with name %s already exists",
                            e.getMessage(), stationName));
        }
    }

    private Station findOneBy(long stationID) {
        try {
            return stationDao.findByID(stationID);
        } catch (DataAccessException e) {
            throw new NoSuchElementException(
                    String.format("%s\nCould not find station with id: %d",
                            e.getMessage(), stationID));
        }
    }
}
