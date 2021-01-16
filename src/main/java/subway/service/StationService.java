package subway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import subway.dao.StationDao;
import subway.domain.Station;
import subway.request.StationRequest;
import subway.response.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {
    private final StationDao stationDao;

    @Autowired
    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse createStation(StationRequest stationRequest) {
        Station newStation = stationDao.save(stationRequest.getDomain());
        return new StationResponse(newStation);
    }

    public List<StationResponse> getStations() {
        return stationDao.findAll().stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
    }

    public boolean deleteStation(Long id) {
            return stationDao.deleteById(id);
    }
}
