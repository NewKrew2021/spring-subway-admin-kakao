package subway.service;

import org.springframework.stereotype.Service;
import subway.dao.StationDao;
import subway.domain.station.Station;
import subway.exception.NotExistException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {
    static final String NOT_EXIST_STATION_ERROR_MESSAGE = "해당 역이 존재하지 않습니다.";

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station createStation(String stationName) {
        Station newStation = stationDao.save(new Station(stationName));
        return newStation;
    }

    public List<Station> getAllStations() {
        return stationDao.findAll().stream()
                .collect(Collectors.toList());
    }

    public Station getStation(long stationId) {
        Station station = stationDao.findById(stationId);
        if (station == null) {
            throw new NotExistException(NOT_EXIST_STATION_ERROR_MESSAGE);
        }
        return station;
    }

    public void deleteStation(long id) {
        stationDao.deleteById(id);
    }

}
