package subway.station;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.exception.ExistStationSaveException;
import subway.exception.TooFewSectionAsDeleteException;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class StationController {

    private final StationDao stationDao;

    public StationController(StationDao stationDao) {
        this.stationDao = stationDao;
    }


    @PostMapping("/stations")
    public ResponseEntity<StationResponse> createStation(@RequestBody StationRequest stationRequest) {
        if( stationDao.hasStationName(stationRequest.getName()) ) {
            throw new ExistStationSaveException();
        }
        Long id = stationDao.save(new Station(stationRequest.getName()));
        StationResponse stationResponse = new StationResponse(id, stationRequest.getName());
        return ResponseEntity.created(URI.create("/stations/" + id)).body(stationResponse);
    }

    @GetMapping(value = "/stations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StationResponse>> showStations() {
        List<StationResponse> stationResponses = stationDao.findAll().stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(stationResponses);
    }

    @DeleteMapping("/stations/{id}")
    public ResponseEntity deleteStation(@PathVariable Long id) {
        stationDao.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(ExistStationSaveException.class)
    private ResponseEntity<?> handleExistStationSaveException() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

}
