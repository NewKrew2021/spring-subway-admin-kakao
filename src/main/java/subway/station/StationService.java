package subway.station;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import subway.exceptions.DuplicateStationNameException;
import subway.exceptions.InvalidStationArgumentException;

import java.util.List;

@Service
public class StationService {

    public static final String DUPLICATE_STATION_NAME_ERROR_MESSAGE = "중복된 역 이름입니다.";
    public static final String NO_MATCHING_STATION_ERROR_MESSAGE = "해당되는 역이 존재하지 않습니다.";
    public static final int NO_UPDATED_ROW = 0;

    private StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station save(Station station) {
        try {
            return stationDao.save(station).orElseThrow(() -> new InvalidStationArgumentException(NO_MATCHING_STATION_ERROR_MESSAGE));
        } catch(DuplicateKeyException e) {
            throw new DuplicateStationNameException(DUPLICATE_STATION_NAME_ERROR_MESSAGE);
        }
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    public void deleteById(Long id) {
        if(stationDao.deleteById(id) == NO_UPDATED_ROW) {
            throw new InvalidStationArgumentException(NO_MATCHING_STATION_ERROR_MESSAGE);
        }
    }

    public Station findById(Long id) { return stationDao.findById(id).orElseThrow(() -> new InvalidStationArgumentException(NO_MATCHING_STATION_ERROR_MESSAGE)); }

}
