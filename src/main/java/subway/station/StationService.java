package subway.station;

import org.springframework.stereotype.Service;
import subway.exception.DuplicateNameException;
import subway.exception.NotExistException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station createStation(Station station) {
        validateUniqueName(station);
        return stationDao.save(station);
    }

    public List<StationResponse> getAllStations() {
        return stationDao.findAll().stream()
                .map(station -> new StationResponse(station.getId(), station.getName()))
                .collect(Collectors.toList());
    }

    public List<Station> convertIdsToStations(List<Long> ids) {
        return ids.stream()
                .map(this::findById)
                .collect(Collectors.toList());
    }

    public void deleteStation(long id) {
        if (stationDao.deleteById(id) == 0) {
            throw new NotExistException("해당 역이 존재하지 않습니다.");
        }
    }

    private void validateUniqueName(Station station) {
        if (stationDao.countByName(station.getName()) != 0) {
            throw new DuplicateNameException("중복된 역 이름입니다.");
        }
    }

    private Station findById(Long id) {
        Station station = stationDao.findById(id);
        if (station == null) {
            throw new NotExistException("해당 역이 존재하지 않습니다.");
        }
        return station;
    }
}
