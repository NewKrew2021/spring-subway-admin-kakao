package subway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import subway.dao.StationDao;
import subway.domain.Station;
import subway.exception.DuplicateNameException;

import java.util.List;

@Service
public class StationService {
    private final StationDao stationDao;

    @Autowired
    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public void insertStation(Station station) {
        if (checkDuplicatedStationName(station.getName())) {
            throw new DuplicateNameException();
        }
        stationDao.save(station);
    }

    public boolean checkDuplicatedStationName(String name) {
        return stationDao.hasSameStationName(name);
    }

    public Station findStationByName(String name) {
        return stationDao.findByName(name);
    }

    public List<Station> findAllStations() {
        return stationDao.findAll();
    }

    public void deleteStation(Long id) {
        stationDao.deleteById(id);
    }

}
