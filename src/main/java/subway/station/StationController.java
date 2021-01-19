package subway.station;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.exceptions.DuplicateStationNameException;
import subway.exceptions.InvalidStationArgumentException;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("stations")
public class StationController {
    @Autowired
    private StationService stationService;

    @ExceptionHandler({DuplicateStationNameException.class, InvalidStationArgumentException.class})
    public ResponseEntity<String> errorHandler(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @PostMapping
    public ResponseEntity<StationResponse> createStation(@RequestBody StationRequest stationRequest) {
        Station newStation = stationService.save(new Station(stationRequest.getName()));
        StationResponse stationResponse = new StationResponse(newStation.getId(), newStation.getName());
        return ResponseEntity.created(URI.create("/stations/" + newStation.getId())).body(stationResponse);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StationResponse>> showStations() {
        List<StationResponse> responses = new ArrayList<>();
        for (Station station : stationService.findAll()) {
            responses.add(new StationResponse(station.getId(), station.getName()));
        }
        return ResponseEntity.ok().body(responses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteStation(@PathVariable Long id) {
        stationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
