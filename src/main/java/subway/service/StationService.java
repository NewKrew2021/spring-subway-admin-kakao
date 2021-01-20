package subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.station.Station;
import subway.exception.InvalidStationException;
import subway.exception.NotExistException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {
    static final String NOT_EXIST_STATION_ERROR_MESSAGE = "해당 역이 존재하지 않습니다.";
    static final String CANNOT_DELETE_STATION_ERROR_MESSAGE = "해당 역을 참조하는 구간이 존재합니다.";

    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public StationService(StationDao stationDao,SectionDao sectionDao) {
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
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

    @Transactional
    public void deleteStation(long id) {
        if (!sectionDao.findByStationId(id).isEmpty()){
            throw new InvalidStationException(CANNOT_DELETE_STATION_ERROR_MESSAGE);
        }
        stationDao.deleteById(id);
    }

}
