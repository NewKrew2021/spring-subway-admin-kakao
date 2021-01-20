package subway.station;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/stations")
public class StationController {
    private final StationDao stationDao;

    public StationController(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @PostMapping
    public ResponseEntity<StationResponse> createStation(@RequestBody StationRequest stationRequest) {
        Station newStation = stationDao.insert(new Station(stationRequest.getName()));

        StationResponse response = newStation.toDto();
        return ResponseEntity.created(URI.create("/stations/" + response.getID())).body(response);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StationResponse>> showStations() {
        Stations stations = new Stations(stationDao.findAll());
        return ResponseEntity.ok(stations.allToDto());
    }

    @GetMapping(value = "/{stationID}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StationResponse> showStation(@PathVariable Long stationID) {
        Station station = stationDao.findByID(stationID);
        return ResponseEntity.ok(station.toDto());
    }

    @DeleteMapping("/{stationID}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long stationID) {
        stationDao.deleteByID(stationID);
        return ResponseEntity.noContent().build();
    }
}
