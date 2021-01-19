package subway.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.convertor.StationConvertor;
import subway.dto.StationRequest;
import subway.dto.StationResponse;
import subway.factory.StationFactory;
import subway.service.StationService;

import java.net.URI;
import java.util.List;



@RestController
public class StationController {

    private StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping("/stations")
    public ResponseEntity<StationResponse> createStation(@RequestBody StationRequest stationRequest) {
        StationResponse stationResponse = StationConvertor.convertStation(stationService.save(StationFactory.getStation(stationRequest)));
        return ResponseEntity.created(URI.create("/stations/" + stationResponse.getId())).body(stationResponse);
    }

    @GetMapping(value = "/stations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StationResponse>> showStations() {
        return ResponseEntity.ok(StationConvertor.convertStations(stationService.findAll()));
    }

    @GetMapping(value = "/stations/{stationId}")
    public ResponseEntity<StationResponse> showStation(@PathVariable Long stationId) {
        return ResponseEntity.ok(StationConvertor.convertStation(stationService.findOne(stationId)));
    }

    @DeleteMapping("/stations/{stationId}")
    public ResponseEntity deleteStation(@PathVariable Long stationId) {
        if (!stationService.deleteById(stationId))
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok().build();
    }


}
