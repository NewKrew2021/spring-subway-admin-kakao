package subway.station;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class StationController {

    private StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping("/stations")
    public ResponseEntity<StationResponse> createStation(@RequestBody StationRequest stationRequest) {
        Station station = stationService.save(new Station(stationRequest));
        return ResponseEntity.created(URI.create("/stations/" + station.getId())).body(new StationResponse(station));
    }

    @GetMapping(value = "/stations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StationResponse>> showStations() {
        return ResponseEntity.ok(stationService.findAll()
                .stream()
                .map(StationResponse::new)
                .collect(Collectors.toList()));
    }

    @GetMapping(value = "/stations/{stationId}")
    public ResponseEntity<StationResponse> showStation(@PathVariable Long stationId) {

        return ResponseEntity.ok(new StationResponse(stationService.findOne(stationId)));
    }

    @DeleteMapping("/stations/{stationId}")
    public ResponseEntity deleteStation(@PathVariable Long stationId) {
        if (!stationService.deleteById(stationId))
            return ResponseEntity.badRequest().build();
        return ResponseEntity.noContent().build();
    }


}
