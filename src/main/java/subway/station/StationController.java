package subway.station;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.station.dto.StationRequest;
import subway.station.dto.StationResponse;
import subway.station.vo.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/stations")
public class StationController {
    private final StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping
    public ResponseEntity<StationResponse> createStation(@RequestBody StationRequest stationRequest) {
        StationResultValue resultValue = stationService.create(new StationCreateValue(stationRequest));

        StationResponse response = resultValue.toResponse();
        return ResponseEntity.created(URI.create("/stations/" + response.getID())).body(response);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StationResponse>> showStations() {
        StationResultValues resultValues = stationService.findAll();
        return ResponseEntity.ok(resultValues.allToResponses());
    }

    @GetMapping(value = "/{stationID}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StationResponse> showStation(@PathVariable Long stationID) {
        StationResultValue resultValue = stationService.findByID(new StationReadValue(stationID));
        return ResponseEntity.ok(resultValue.toResponse());
    }

    @DeleteMapping("/{stationID}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long stationID) {
        stationService.deleteByID(new StationDeleteValue(stationID));
        return ResponseEntity.noContent().build();
    }
}
