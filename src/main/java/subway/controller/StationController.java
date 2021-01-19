package subway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.domain.Station;
import subway.dto.StationRequest;
import subway.dto.StationResponse;
import subway.exception.DuplicateStationNameException;
import subway.exception.StationNotFoundException;
import subway.service.StationService;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

        try{
            stationService.insertStation(station);
            Station newStation = stationService.findStationByName(station.getName());
            StationResponse stationResponse = new StationResponse(newStation);
            return ResponseEntity.created(URI.create("/stations/" + newStation.getId())).body(stationResponse);
        }
        catch (DuplicateStationNameException e){
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(value = "/stations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StationResponse>> showStations() {
        List<StationResponse> StationResponses;
        List<Station> stations = stationService.findAllStations();
        StationResponses = stations
                .stream()
                .map(station -> new StationResponse(station.getId(), station.getName()))
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(StationResponses);
    }

    @DeleteMapping("/stations/{id}")
    public ResponseEntity deleteStation(@PathVariable Long id) {
        try{
            stationService.deleteStation(id);
            return ResponseEntity.noContent().build();
        }
        catch (StationNotFoundException e){
            return ResponseEntity.badRequest().build();
        }
    }
}
