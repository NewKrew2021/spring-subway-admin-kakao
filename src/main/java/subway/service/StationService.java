package subway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import subway.dao.StationDao;
import subway.dto.Station;
import subway.exception.DuplicateStationNameException;
import subway.exception.StationNotFoundException;

import java.util.List;

@Service
public class StationService {

    private final StationDao stationDao;

    @Autowired
    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public void insertStation(Station station) throws DuplicateStationNameException {
        if (checkDuplicatedStationName(station.getName())) {
            throw new DuplicateStationNameException();
        }
        stationDao.save(station);
    }

    public boolean checkDuplicatedStationName(String name) {
        return stationDao.hasStationName(name);
    }

    public Station findStationByName(String name) {
        return stationDao.findByName(name);
    }

    public List<Station> findAllStations() {
        return stationDao.findAll();
    }

    public void deleteStation(Long id) throws StationNotFoundException {
        if(!stationDao.hasStationId(id)){
            throw new StationNotFoundException();
        }
        stationDao.delete(id);
    }
}
