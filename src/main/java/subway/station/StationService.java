package subway.station;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import subway.station.domain.Station;
import subway.station.vo.StationResultValue;
import subway.station.vo.StationResultValues;

import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class StationService {
    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResultValue createWithName(String stationName) {
        Station station;

        try {
            station = stationDao.insert(new Station(stationName));
        } catch (DataAccessException e) {
            throw new IllegalArgumentException(
                    String.format("%s\nStation with name %s already exists",
                            e.getMessage(), stationName));
        }

        return station.toResultValue();
    }

    public StationResultValues findAll() {
        return new StationResultValues(stationDao.findAll()
                .stream()
                .map(station -> new StationResultValue(station.getID(), station.getName()))
                .collect(Collectors.toList()));
    }

    public StationResultValue findByID(long stationID) {
        Station station;

        try {
            station = stationDao.findByID(new Station(stationID));
        } catch (DataAccessException e) {
            throw new NoSuchElementException(
                    String.format("%s\nCould not find station with id: %d",
                            e.getMessage(), stationID));
        }

        return station.toResultValue();
    }

    public void deleteByID(long stationID) {
        stationDao.deleteByID(stationID);
    }
}
