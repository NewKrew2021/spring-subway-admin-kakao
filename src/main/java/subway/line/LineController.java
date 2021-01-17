package subway.line;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.station.StationDao;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class LineController {

    private final LineDao lineDao = LineDao.getInstance();
    private final StationDao stationDao;

    public LineController(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @PostMapping(value = "/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(),
                lineRequest.getColor(),
                stationDao.findOne(lineRequest.getUpStationId()),
                stationDao.findOne(lineRequest.getDownStationId()),
                lineRequest.getDistance());
        Line newLine = lineDao.save(line);
        LineResponse lineResponse = new LineResponse(newLine);
        return ResponseEntity.created(URI.create("/lines/" + newLine.getId())).body(lineResponse);
    }

    @GetMapping("/lines")
    public ResponseEntity<List<LineResponse>> showAllLines() {
        List<LineResponse> responses = lineDao.findAll().stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(responses);
    }

    @GetMapping("/lines/{id}")
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        Line line = lineDao.findOne(id);
        LineResponse response = new LineResponse(line);
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/lines/{id}")
    public ResponseEntity<LineResponse> updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(),
                lineRequest.getColor(),
                stationDao.findOne(lineRequest.getUpStationId()),
                stationDao.findOne(lineRequest.getDownStationId()),
                lineRequest.getDistance());
        LineResponse response = new LineResponse(lineDao.update(id, line));
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity<LineResponse> deleteLine(@PathVariable Long id) {
        lineDao.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/lines/{lineId}/sections")
    public ResponseEntity<LineResponse> deleteStationOnLine(@PathVariable Long lineId, @RequestParam("stationId") Long stationId) {
        Line line = lineDao.findOne(lineId);
        line.deleteStation(stationId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/lines/{id}/sections")
    public ResponseEntity<SectionResponse> createSectionOnLine(@PathVariable Long id, @RequestBody SectionRequest sectionRequest) {
        Line line = lineDao.findOne(id);
        SectionResponse response = new SectionResponse(line.addSection(stationDao.findOne(sectionRequest.getUpStationId()),
                stationDao.findOne(sectionRequest.getDownStationId()),
                sectionRequest.getDistance()));
        return ResponseEntity.ok().body(response);
    }


}
