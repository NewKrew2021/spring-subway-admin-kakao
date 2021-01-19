package subway.station.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.station.dao.StationDao;
import subway.station.domain.StationRequest;
import subway.station.domain.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {
    private final StationDao stationDao;

    @Autowired
    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @Transactional
    public void deleteById(Long id) {
        stationDao.deleteById(id);
    }


    public List<StationResponse> findAll() {
        return stationDao.findAll()
                .stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public StationResponse save(StationRequest stationRequest) {
        return StationResponse.of(stationDao.save(stationRequest.getName()));
    }

    public StationResponse findById(Long id) {
        return StationResponse.of(stationDao.findById(id));
    }
}
