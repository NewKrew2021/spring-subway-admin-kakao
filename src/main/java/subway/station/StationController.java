package subway.station;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/stations")
public class StationController {

    StationService stationService;

    @Autowired
    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping
    public ResponseEntity<StationResponse> createStation(@RequestBody StationRequest stationRequest) {
        Station station = Station.of(stationRequest.getName());
        Station newStation = stationService.save(station);
        StationResponse stationResponse = StationResponse.of(newStation);
        return ResponseEntity.created(URI.create("/stations/" + newStation.getId()))
                .body(stationResponse);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StationResponse>> showStations() {
        List<Station> stations = stationService.findAll();
        List<StationResponse> stationResponses = stations.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(stationResponses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteStation(@PathVariable Long id) {
        stationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity exceptionHandler(Exception e) {
        e.printStackTrace();

        return ResponseEntity.badRequest().body(e.getMessage());
    }

}
