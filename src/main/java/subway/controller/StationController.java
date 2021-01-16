package subway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
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
        try {
            StationResponse stationResponse = stationService.createStation(stationRequest);
            return ResponseEntity.created(URI.create("/stations/" + stationResponse.getId())).body(stationResponse);
        } catch (DataAccessException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StationResponse>> getStations() {
        List<StationResponse> response = stationService.getStations();
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteStation(@PathVariable Long id) {
        boolean response = stationService.deleteStation(id);
        return response ? ResponseEntity.noContent().build() : ResponseEntity.badRequest().build();
    }
}
