package subway.station;

import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class StationService {
    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse create(StationRequest request) {
        stationDao.validateName(request.getName());

        Station newStation = stationDao.insert(request.getName());
        return newStation.toDto();
    }

    public List<StationResponse> findAll() {
        return stationDao.findAll()
                .stream()
                .map(Station::toDto)
                .collect(toList());
    }

    public StationResponse findById(Long id) {
        return stationDao.findById(id).toDto();
    }

    public boolean deleteById(Long id) {
        return stationDao.deleteById(id);
    }
}
