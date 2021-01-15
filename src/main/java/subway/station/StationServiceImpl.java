package subway.station;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationServiceImpl implements StationService {
    private StationDao stationDao;

    public StationServiceImpl(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse save(StationRequest stationRequest) {
        Station station = new Station(stationRequest.getName());
        Station newStation = stationDao.save(station);
        return new StationResponse(newStation.getId(), newStation.getName());
    }

    public List<StationResponse> findAll() {
        return stationDao.findAll().stream().map(res -> new StationResponse(res.getId(), res.getName())).collect(Collectors.toList());
    }

    public StationResponse findOne(Long stationId) {
        Station station = stationDao.findOne(stationId);
        return new StationResponse(station.getId(), station.getName());
    }

    public boolean deleteById(Long stationId) {
        return stationDao.deleteById(stationId) != 0;
    }

}
