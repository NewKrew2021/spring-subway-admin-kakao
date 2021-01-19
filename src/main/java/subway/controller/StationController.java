package subway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.domain.Station;
import subway.domain.StationRequest;
import subway.domain.StationResponse;
import subway.service.StationService;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
public class StationController {
    private final StationService stationService;

    @Autowired
    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping("/stations")
    public ResponseEntity<StationResponse> createStation(@RequestBody StationRequest stationRequest) {
        Station station = new Station(stationRequest.getName());
        if (!stationService.insertStation(station)) {
            return ResponseEntity.badRequest().build();
        }

        Station newStation = stationService.findStationByName(station.getName());

        StationResponse stationResponse = new StationResponse(newStation.getId(), newStation.getName());
        return ResponseEntity.created(URI.create("/stations/" + newStation.getId())).body(stationResponse);
    }

    @GetMapping(value = "/stations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StationResponse>> showStations() {
        List<StationResponse> StationResponses = new ArrayList<>();
        List<Station> stations = stationService.findAllStations();
        for (Station station : stations) {
            StationResponses.add(new StationResponse(station.getId(), station.getName()));
        }

        return ResponseEntity.ok().body(StationResponses);
    }

    @DeleteMapping("/stations/{id}")
    public ResponseEntity deleteStation(@PathVariable Long id) {
        stationService.deleteStation(id);
        return ResponseEntity.noContent().build();
    }
}
