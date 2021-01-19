package subway.station;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StationService {
    @Autowired
    private StationDao stationDao;

    public Station save(Station station) {
        return stationDao.save(station);
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    public boolean deleteById(Long id) {
        if(stationDao.deleteById(id) == 1) {
            return true;
        }
        return false;
    }

}
