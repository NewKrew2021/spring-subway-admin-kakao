package subway.station;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class StationService {

    private final StationDao stationDao;

    @Autowired
    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station create(Station station) {
        return stationDao.save(station);
    }

    public List<Station> showAll() {
        return stationDao.findAll();
    }

    public void deleteById(Long id) {
        stationDao.deleteById(id);
    }
}
