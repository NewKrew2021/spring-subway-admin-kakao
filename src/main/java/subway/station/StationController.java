package subway.station;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
public class StationController {
    private final StationDao stationDao;

    public StationController(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @PostMapping("/stations")
    public ResponseEntity<StationResponse> createStation(@RequestBody StationRequest stationRequest) {
        Station newStation = stationDao.insert(new Station(stationRequest.getName()));
        if (newStation == null) {
            return ResponseEntity.badRequest().build();
        }

        StationResponse response = newStation.toDto();
        return ResponseEntity.created(URI.create("/stations/" + response.getId())).body(response);
    }

    @GetMapping(value = "/stations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StationResponse>> showStations() {
        Stations stations = new Stations(stationDao.findAll());
        return ResponseEntity.ok(stations.toDto());
    }

    @GetMapping(value = "/stations/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StationResponse> showStation(@PathVariable Long id) {
        Station station = stationDao.findById(id);
        if (station == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(station.toDto());
    }

    @DeleteMapping("/stations/{id}")
    public ResponseEntity<?> deleteStation(@PathVariable Long id) {
        boolean deleted = stationDao.deleteById(id);
        if (!deleted) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.noContent().build();
    }
}
