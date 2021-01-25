package subway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.StationDao;
import subway.domain.station.Station;
import subway.controller.station.StationRequest;
import subway.domain.station.Stations;
import subway.exception.id.InvalidStationIdException;

@Service
public class StationService {
    private final StationDao stationDao;

    @Autowired
    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station createStation(StationRequest stationRequest) {
        Station station = new Station(stationRequest.getName());
        return stationDao.save(station);
    }

    public Stations findAll() {
        return new Stations(stationDao.findAll());
    }

    public Station find(Long id) {
        return stationDao.getById(id);
    }

    @Transactional
    public boolean deleteStation(Long id) {
        if(!stationDao.contain(id)) {
            throw new InvalidStationIdException(id);
        }
        return stationDao.deleteById(id);
    }

}
