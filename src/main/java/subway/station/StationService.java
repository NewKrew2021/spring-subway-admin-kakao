package subway.station;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import subway.station.exceptions.DuplicateStationNameException;
import subway.station.exceptions.InvalidStationDeleteException;
import subway.station.exceptions.NoSuchStationException;

import java.util.List;
import java.util.Optional;

@Service
public class StationService {

    StationDao stationDao;

    @Autowired
    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station save(Station station) {
        String stationName = station.getName();
        checkDuplicateName(stationName);

        try {
            return stationDao.save(station);
        } catch (DuplicateKeyException e) {
            throw new DuplicateStationNameException(stationName);
        }
    }

    private void checkDuplicateName(String name) {
        if (stationDao.findByName(name) != null) {
            throw new DuplicateStationNameException(name);
        }
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    public Station find(Long id) {
        return Optional.ofNullable(stationDao.findById(id))
                .orElseThrow(() -> new NoSuchStationException(id));
    }

    public void delete(Long id) {
        try {
            checkExistStation(id);
            stationDao.deleteById(id);
        } catch (Exception e) {
            System.out.println(e.getClass());
            throw new InvalidStationDeleteException(id);
        }
    }

    private void checkExistStation(Long id) {
        if (stationDao.findById(id) == null) {
            throw new InvalidStationDeleteException(id);
        }
    }
}
