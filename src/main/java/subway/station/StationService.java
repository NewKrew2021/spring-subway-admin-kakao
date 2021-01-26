package subway.station;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import subway.station.exceptions.DuplicateStationNameException;
import subway.station.exceptions.InvalidStationDeleteException;
import subway.station.exceptions.NotFoundStationException;

import java.util.List;

@Service
public class StationService {

    StationDao stationDao;

    @Autowired
    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station save(Station station) {
        String stationName = station.getName();
        if (stationDao.isDuplicateName(stationName)) {
            throw new DuplicateStationNameException(stationName);
        }

        try {
            return stationDao.save(station);
        } catch (DuplicateKeyException e) {
            throw new DuplicateStationNameException(stationName);
        }
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    public Station find(Long id) {
        try {
            return stationDao.findById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundStationException(id);
        }
    }

    public void delete(Long id) {
        try {
            stationDao.deleteById(id);
        } catch (Exception e) {
            throw new InvalidStationDeleteException(id);
        }
    }
}
