package subway.station.web;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.station.dto.StationRequest;
import subway.station.dto.StationResponse;
import subway.station.entity.Station;
import subway.station.service.StationService;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class StationController {
    private final StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping("/stations")
    public ResponseEntity<StationResponse> createStation(@RequestBody StationRequest stationRequest) {
        Station station = stationService.create(stationRequest.getName());
        return ResponseEntity.created(
                URI.create(
                        "/stations/" + station.getId()
                )
        ).body(new StationResponse(station));
    }

    @GetMapping(value = "/stations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StationResponse>> showStations() {
        return ResponseEntity.ok(
                stationService.getAllStations()
                        .stream()
                        .map(StationResponse::new)
                        .collect(Collectors.toList())
        );
    }

    @DeleteMapping("/stations/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        stationService.delete(id);
        return ResponseEntity.noContent()
                .build();
    }
}
