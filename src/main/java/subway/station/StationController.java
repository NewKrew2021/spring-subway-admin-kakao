package subway.station;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.station.domain.Station;
import subway.station.dto.StationRequest;
import subway.station.dto.StationResponse;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/stations")
public class StationController {
    private final StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping
    public ResponseEntity<StationResponse> createStation(@RequestBody StationRequest stationRequest) {
        Station station = stationService.createWithName(stationRequest.getName());

        StationResponse response = StationResponse.of(station);
        return ResponseEntity.created(URI.create("/stations/" + response.getID())).body(response);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StationResponse>> showStations() {
        List<StationResponse> stationResponses = stationService.findAll()
                .stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());

        return ResponseEntity.ok(stationResponses);
    }

    @GetMapping(value = "/{stationID}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StationResponse> showStation(@PathVariable Long stationID) {
        Station station = stationService.findByID(stationID);
        return ResponseEntity.ok(StationResponse.of(station));
    }

    @DeleteMapping("/{stationID}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long stationID) {
        stationService.deleteByID(stationID);
        return ResponseEntity.noContent().build();
    }
}
