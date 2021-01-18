package subway.station;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.exception.InvalidIdException;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/stations")
public class StationController {
    private final StationService stationService;
    private final StationDao stationDao;

    @Autowired
    public StationController(StationService stationService, StationDao stationDao) {
        this.stationService = stationService;
        this.stationDao = stationDao;
    }

    @ExceptionHandler(value = {DataAccessException.class, InvalidIdException.class})
    public ResponseEntity exceptionHandler() {
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("")
    public ResponseEntity<StationResponse> createStation(@RequestBody StationRequest stationRequest) {
        Station newStation = stationService.createStation(stationRequest);
        StationResponse stationResponse = new StationResponse(newStation);
        return ResponseEntity.created(URI.create("/stations/" + newStation.getId())).body(stationResponse);
    }

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StationResponse>> showStations() {
        List<StationResponse> response = stationService.findAll().stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteStation(@PathVariable Long id) {
        if(stationService.deleteStation(id)) {
            throw new InvalidIdException("해당하는 ID가 없습니다. : Station ID : " + id);
        }
        return ResponseEntity.noContent().build();
    }
}
