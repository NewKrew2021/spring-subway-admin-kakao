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
        StationResponse stationResponse = stationService.saveAndResponse(stationRequest);
        return ResponseEntity.created(URI.create("/stations/" + stationResponse.getId())).body(stationResponse);
    }

    @GetMapping(value = "/stations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StationResponse>> showStations() {
        return ResponseEntity.ok(stationService.findAllResponse());
    }

    @GetMapping(value = "/stations/{stationId}")
    public ResponseEntity<StationResponse> showStation(@PathVariable Long stationId) {
        return ResponseEntity.ok(stationService.findOneResponse(stationId));
    }

    @DeleteMapping("/stations/{stationId}")
    public ResponseEntity deleteStation(@PathVariable Long stationId) {
        if (!stationService.deleteById(stationId))
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok().build();
    }


}
