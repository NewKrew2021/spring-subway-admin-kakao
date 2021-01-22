package subway.station.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.station.dto.StationRequest;
import subway.station.dto.StationResponse;
import subway.station.service.StationService;

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

    @PostMapping
    public ResponseEntity<StationResponse> createStation(@RequestBody StationRequest stationRequest) {
        StationResponse response = stationService.create(stationRequest);
        return ResponseEntity.created(URI.create("/stations/" + response.getId())).body(response);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StationResponse>> showStations() {
        return ResponseEntity.ok(stationService.findAll());
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StationResponse> showStation(@PathVariable Long id) {
        StationResponse response = stationService.findById(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStation(@PathVariable Long id) {
        stationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
