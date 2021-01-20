package subway.station;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class StationController {
    private StationDao stationDao;

    public StationController() {
        this.stationDao = new StationDao();
    }

    @PostMapping("/stations")
    public ResponseEntity<StationResponse> createStation(@RequestBody StationRequest stationRequest) {
        Station station = new Station(stationRequest.getName());
        Station newStation = stationDao.save(station);
        StationResponse stationResponse = new StationResponse(newStation.getId(), newStation.getName());
        return ResponseEntity.created(URI.create("/stations/" + newStation.getId())).body(stationResponse);
    }

    @GetMapping(value = "/stations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StationResponse>> showStations() {
        List<StationResponse> res = stationDao.findAll()
                .stream()
                .map(station -> new StationResponse(station.getId(), station.getName()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/stations/{id}")
    public ResponseEntity<?> deleteStation(@PathVariable Long id) {
        stationDao.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
