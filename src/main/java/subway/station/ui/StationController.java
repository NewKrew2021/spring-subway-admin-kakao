package subway.station.ui;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.station.dao.StationDao;
import subway.station.dto.StationRequest;
import subway.station.dto.StationResponse;
import subway.station.domain.Station;

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
        Station station = new Station(stationRequest.getName()); // 여기상태에서는 id가 없다.
        Station newStation = stationDao.save(station);
        StationResponse stationResponse = new StationResponse(newStation.getId(), newStation.getName());
        return ResponseEntity.created(URI.create("/stations/" + newStation.getId())).body(stationResponse);
    }

    @GetMapping(value = "/stations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StationResponse>> showStations() {
        List<Station> stations = stationDao.findAll();
        List<StationResponse> stationResponses = stations.stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(stationResponses);
    }

    @DeleteMapping("/stations/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        stationDao.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
