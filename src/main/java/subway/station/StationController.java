package subway.station;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/stations")
public class StationController {

    @Resource
    private StationService stationService;

    @PostMapping
    public ResponseEntity<StationResponse> createStation(@RequestBody StationRequest stationRequest) {
        try {
            Station station = stationRequest.getStation();
            Long stationId = stationService.create(station);
            StationResponse stationResponse = new StationResponse(stationId, station.getName());

            return ResponseEntity.created(URI.create("/stations/" + stationId)).body(stationResponse);
        } catch (DuplicateKeyException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StationResponse>> showStations() {
        List<Station> stations = stationService.getStations();

        return ResponseEntity.ok().body(StationResponse.getStationResponses(stations));
    }

    @DeleteMapping("/{stationId}")
    public ResponseEntity deleteStation(@PathVariable Long stationId) {
        stationService.delete(stationId);

        return ResponseEntity.noContent().build();
    }
}
