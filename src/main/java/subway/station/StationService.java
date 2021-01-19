package subway.station;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.URI;
import java.util.List;

@Service
public class StationService {

    @Resource
    public StationDao stationDao;

    public ResponseEntity<StationResponse> createStation(StationRequest stationRequest) {
        try {
            stationDao.save(new Station(stationRequest.getName()));
        } catch (DuplicateKeyException e) {
            return ResponseEntity.badRequest().build();
        }

        Station newStation = stationDao.findByName(stationRequest.getName());

        return ResponseEntity.created(URI.create("/stations/" + newStation.getId()))
                .body(new StationResponse(newStation.getId(), newStation.getName()));
    }

    public ResponseEntity<List<StationResponse>> showStations() {
        List<Station> stations = stationDao.findAll();

        return ResponseEntity.ok().body(StationResponse.getStationResponses(stations));
    }

    public ResponseEntity deleteStation(Long id) {
        stationDao.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
