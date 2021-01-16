package subway.station;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
public class StationController {
    @Autowired
    StationDao stationDao;


    @PostMapping("/stations")
    public ResponseEntity<StationResponse> createStation(@RequestBody StationRequest stationRequest) {
        Station station = new Station(stationRequest.getName());
        if(stationDao.hasSameStationName(station)){
            return ResponseEntity.badRequest().build();
        }

        stationDao.save(station);

        Station newStation = stationDao.findByName(station.getName());

        StationResponse stationResponse = new StationResponse(newStation.getId(), newStation.getName());
        return ResponseEntity.created(URI.create("/stations/" + newStation.getId())).body(stationResponse);
    }

    @GetMapping(value = "/stations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StationResponse>> showStations() {
        List<StationResponse> StationResponses=new ArrayList<>();
        List<Station> stations = stationDao.findAll();
        for (Station station : stations) {
            StationResponses.add(new StationResponse(station.getId(), station.getName()));
        }

        return ResponseEntity.ok().body(StationResponses);
    }

    @DeleteMapping("/stations/{id}")
    public ResponseEntity deleteStation(@PathVariable Long id) {
        stationDao.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
