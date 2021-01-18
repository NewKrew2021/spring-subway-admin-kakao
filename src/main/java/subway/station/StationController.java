package subway.station;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class StationController {
    private final StationDao stationDao;

    public StationController(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @PostMapping("/stations")
    public ResponseEntity<StationResponse> createStation(@RequestBody StationRequest stationRequest) {
        if (stationDao.existsBy(stationRequest.getName())) {
            throw new IllegalArgumentException("이미 등록된 지하철역 입니다.");
        }
        Station station = new Station(stationRequest.getName());
        Station newStation = stationDao.save(station);
        StationResponse stationResponse = new StationResponse(newStation.getId(), newStation.getName());
        return ResponseEntity.created(URI.create("/stations/" + newStation.getId())).body(stationResponse);
    }

    @GetMapping(value = "/stations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StationResponse>> showStations() {
        return stationDao.findAll()
                .stream()
                .map(station -> new StationResponse(station.getId(), station.getName()))
                .collect(Collectors.collectingAndThen(Collectors.toList(), ResponseEntity::ok));
    }

    @DeleteMapping("/stations/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        stationDao.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
