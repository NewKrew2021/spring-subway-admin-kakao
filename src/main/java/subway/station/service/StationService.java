package subway.station.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import subway.station.dao.StationDao;
import subway.station.domain.Station;
import subway.station.domain.StationRequest;
import subway.station.domain.StationResponse;

import javax.annotation.Resource;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {

    @Resource
    public StationDao stationDao;

    public ResponseEntity<StationResponse> createStation(StationRequest stationRequest) {
        stationDao.save(new Station(stationRequest.getName()));

        Station newStation = stationDao.findByName(stationRequest.getName());
        StationResponse stationResponse = new StationResponse(newStation.getId(), newStation.getName());
        return ResponseEntity.created(URI.create("/stations/" + newStation.getId())).body(stationResponse);
    }

    public ResponseEntity<List<StationResponse>> showStations() {
        List<Station> stations = stationDao.findAll();
        List<StationResponse> stationResponses = stations.stream().map(StationResponse::new).collect(Collectors.toList());
        return ResponseEntity.ok().body(stationResponses);
    }

    public ResponseEntity deleteStation(Long id) {
        stationDao.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
