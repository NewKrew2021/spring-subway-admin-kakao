package subway.station;

import org.apache.coyote.Response;
import org.springframework.cglib.core.DuplicatesPredicate;
import subway.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.exceptions.DuplicateStationException;

import subway.section.SectionDao;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class StationController {

    @PostMapping("/stations")
    public ResponseEntity<StationResponse> createStation(@RequestBody StationRequest stationRequest) {
        if (StationDao.getInstance().findByName(stationRequest.getName()).size() != 0){
            throw new DuplicateStationException();
        }
        Station station = new Station(stationRequest.getName());
        Station newStation = StationDao.getInstance().save(station);
        StationResponse stationResponse = new StationResponse(newStation.getId(), newStation.getName());
        return ResponseEntity.created(URI.create("/stations/" + newStation.getId())).body(stationResponse);
    }

    @GetMapping(value = "/stations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StationResponse>> showStations() {
        return ResponseEntity.ok().body(StationDao.getInstance().findAll().stream()
                .map((Station station) -> new StationResponse(station.getId(), station.getName()))
                .collect(Collectors.toList()));
    }

    @DeleteMapping("/stations/{id}")
    public ResponseEntity deleteStation(@PathVariable Long id) {
        StationDao.getInstance().deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(DuplicateStationException.class)
    public ResponseEntity<String> handleDuplicateException(){
        return ResponseEntity.badRequest().body("DuplicateStationException");
    }

    @ExceptionHandler(InvalidValueException.class)
    public ResponseEntity<String> handleInvalidValueException(){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
