package subway.service;

import org.springframework.stereotype.Service;
import subway.dao.StationDao;
import subway.domain.Station;
import subway.exception.DataEmptyException;
import subway.exception.DeleteImpossibleException;

import java.util.List;

@Service
public class StationServiceImpl implements StationService {
    public static final int ZERO = 0;
    private final StationDao stationDao;

    public StationServiceImpl(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @Override
    public Station save(Station station) {
        Station newStation = new Station(station.getName());
        return stationDao.save(newStation);
    }

    @Override
    public List<Station> findAll() {
        List<Station> stations = stationDao.findAll();
        if (stations.size() == ZERO) {
            throw new DataEmptyException();
        }
        return stations;
    }

    @Override
    public Station findOne(Long stationId) {
        Station station = stationDao.findOne(stationId);
        if (station == null) {
            throw new DataEmptyException();
        }
        return station;
    }

    @Override
    public void deleteById(Long stationId) {
        if (stationDao.deleteById(stationId) == ZERO) {
            throw new DeleteImpossibleException();
        }
    }
}
