package subway.station;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {

    @Resource
    public StationDao stationDao;

    public ResponseEntity<StationResponse> createStation(StationRequest stationRequest) {
        Station station = new Station(stationRequest.getName());
        Station newStation = stationDao.save(station);
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
