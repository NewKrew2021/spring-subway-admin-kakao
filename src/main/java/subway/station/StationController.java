package subway.station;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class StationController {

    @Resource
    private StationService stationService;

    @PostMapping("/stations")
    public ResponseEntity<StationResponse> createStation(@RequestBody StationRequest stationRequest) {
        return stationService.create(stationRequest);
    }

    @GetMapping(value = "/stations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StationResponse>> showStations() {
        return stationService.getStations();
    }

    @DeleteMapping("/stations/{stationId}")
    public ResponseEntity deleteStation(@PathVariable Long stationId) {
        return stationService.delete(stationId);
    }
}
