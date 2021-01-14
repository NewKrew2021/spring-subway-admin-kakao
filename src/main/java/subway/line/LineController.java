package subway.line;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.station.Station;
import subway.station.StationController;
import subway.station.StationResponse;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class LineController {

    private static final LineDao lineDao = new LineDao();

    @PostMapping(value = "/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Optional<Line> byName = lineDao.findByName(lineRequest.getName());
        if (byName.isPresent()){
            return ResponseEntity.badRequest().build();
        }

        List<Station> stations = new LinkedList<>();
        stations.add(StationController.stationDao.findById(lineRequest.getUpStationId()));
        stations.add(StationController.stationDao.findById(lineRequest.getDownStationId()));

        Line line = lineDao.save(new Line(lineRequest.getColor(), lineRequest.getName(),
                lineRequest.getUpStationId(), lineRequest.getDownStationId(),lineRequest.getDistance() , stations));
        LineResponse lineResponse = new LineResponse(
                line.getId(),
                line.getName(),
                line.getColor(),
                stations.stream().map(StationResponse::new).collect(Collectors.toList())
        );
        return ResponseEntity.created(URI.create("/stations/" + line.getId())).body(lineResponse);
    }

    @GetMapping("/lines")
    public ResponseEntity<List<LineResponse>> showStationsOfLine(){
        List<LineResponse> lineResponses = new ArrayList<>();
        for (Line line : lineDao.findAll()) {
            lineResponses.add(new LineResponse(
                    line.getId(),
                    line.getName(),
                    line.getColor(),
                    line.getStations()
                            .stream()
                            .map(StationResponse::new)
                            .collect(Collectors.toList())
            ));
        }

        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping("/lines/{id}")
    public ResponseEntity<LineResponse> findLineById(@PathVariable Long id) {
        Line line = lineDao.findById(id).get();

        LineResponse lineResponse = new LineResponse(
                line.getId(),
                line.getName(),
                line.getColor(),
                line.getStations()
                        .stream()
                        .map(StationResponse::new)
                        .collect(Collectors.toList())
        );
        return ResponseEntity.ok().body(lineResponse);
    }

    @PutMapping("lines/{id}")
    public ResponseEntity modifyLine(@PathVariable Long id, @RequestBody LineRequest lineRequest){
        lineDao.modify(id, lineRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity deleteLineById(@PathVariable Long id) {
        lineDao.deleteById(id);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/lines/{id}/sections")
    public ResponseEntity addSection(@PathVariable Long id, @RequestBody SectionRequest sectionRequest) {
        Line line = lineDao.findById(id).get();

        // 상행 종점 등록
       if(line.getUpStationId() == sectionRequest.getDownStationId()) {
            line.getStations().add(0, StationController.stationDao.findById(sectionRequest.getUpStationId()));
            line.setDistance(line.getDistance() + sectionRequest.getDistance());
        }

       return ResponseEntity.ok().build();
    }

}
