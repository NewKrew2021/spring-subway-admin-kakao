package subway.controller;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.controller.to.StationRequest;
import subway.controller.to.StationResponse;
import subway.domain.Station;
import subway.service.LineService;
import subway.service.SectionService;
import subway.service.StationService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/stations")
public class StationController {
    private final StationService stationService;
    private final LineService lineService;
    private final SectionService sectionService;

    public StationController(StationService stationService, LineService lineService, SectionService sectionService) {
        this.stationService = stationService;
        this.lineService = lineService;
        this.sectionService = sectionService;
    }

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
