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
    private final StationDao stationDao;

    @Autowired
    public StationController(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @ExceptionHandler(value = {DataAccessException.class, InvalidIdException.class})
    public ResponseEntity exceptionHandler() {
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("")
    public ResponseEntity<StationResponse> createStation(@RequestBody StationRequest stationRequest) {
        Station station = new Station(stationRequest.getName());
        Station newStation = stationDao.save(station);
        StationResponse stationResponse = new StationResponse(newStation);
        return ResponseEntity.created(URI.create("/stations/" + newStation.getId())).body(stationResponse);
    }

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StationResponse>> showStations() {
        List<StationResponse> response = stationDao.findAll().stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteStation(@PathVariable Long id) {
        boolean response = stationDao.deleteById(id);
        if (!response) {
            throw new InvalidIdException("존재하지 않는 Station ID 입니다. Station ID : " + id);
        }
        return ResponseEntity.noContent().build();
    }
}
