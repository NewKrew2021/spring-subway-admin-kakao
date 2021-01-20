package subway.station.service;

import org.springframework.stereotype.Service;
import subway.station.dao.StationDao;
import subway.station.domain.Station;
import subway.station.domain.StationRequest;
import subway.station.domain.StationResponse;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {

    @Resource
    public StationDao stationDao;

    public StationResponse createStation(StationRequest stationRequest) {
        stationDao.save(new Station(stationRequest.getName()));

        Station newStation = stationDao.findByName(stationRequest.getName());
        return new StationResponse(newStation.getId(), newStation.getName());
    }

    public List<StationResponse> showStations() {
        List<Station> stations = stationDao.findAll();
        return stations.stream().map(StationResponse::new).collect(Collectors.toList());
    }

    public void deleteStation(Long id) {
        stationDao.deleteById(id);
    }

}
