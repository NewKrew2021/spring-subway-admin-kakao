package subway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.request.StationRequest;
import subway.response.StationResponse;
import subway.service.StationService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/stations")
public class StationController {
    private final StationService stationService;

    @Autowired
    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StationResponse> createStation(@RequestBody StationRequest stationRequest) {
        StationResponse stationResponse = stationService.createStation(stationRequest);
        return ResponseEntity.created(URI.create("/stations/" + stationResponse.getId())).body(stationResponse);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StationResponse>> getStations() {
        return ResponseEntity.ok().body(stationService.getStations());
    }

    @DeleteMapping("/{stationId}")
    public ResponseEntity deleteStation(@PathVariable Long stationId) {
        return stationService.deleteStation(stationId) ?
                ResponseEntity.noContent().build() : ResponseEntity.badRequest().build();
    }
}
