package subway.service;

import org.springframework.stereotype.Service;
import subway.dao.StationDao;
import subway.domain.Station;
import subway.exceptions.DuplicateNameException;

import java.util.List;

@Service
public class StationService {

    private StationDao stationDao;

    public StationService (StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station createLine(Station station) {
        if(stationDao.countByName(station.getName()) != 0) {
            throw new DuplicateNameException("이미 존재하는 역입니다.");
        }
        return stationDao.save(station.getName());
    }

    public List<Station> showStations() {
        return stationDao.findAll();
    }

    public void deleteStation(Long id) {
        stationDao.deleteById(id);
    }
}
