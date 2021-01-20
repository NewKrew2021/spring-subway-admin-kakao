package subway.service;

import org.springframework.stereotype.Service;
import subway.dao.StationDao;
import subway.domain.Station;
import subway.exception.DuplicateException;
import subway.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;

@Service
public class StationService {
    private StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station createStation(Station station) {
        if(stationDao.hasDuplicateName(station.getName())) throw new DuplicateException();
        return stationDao.save(station);
    }

    public Station getStation(Long id) {
        return stationDao.findById(id).orElseThrow(NotFoundException::new);
    }

    public List<Station> getStations() {
        return new ArrayList<>(stationDao.findAll());
    }

    public void deleteStation(Long id) {
        stationDao.deleteById(id);
    }
}
