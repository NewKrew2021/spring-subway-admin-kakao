package subway.line;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.station.StationDao;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class LineController {
    private final LineDao lineDao;
    private final StationDao stationDao;

    public LineController(LineDao lineDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
    }

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        Line newLine = lineDao.save(line);
        LineResponse lineResponse = new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor(), null);
        return ResponseEntity.created(URI.create("/lines/" + newLine.getId())).body(lineResponse);
    }

    @GetMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        return ResponseEntity.ok().body(getLineResponses());
    }

    @DeleteMapping("/lines/{lineId}")
    public ResponseEntity deleteLine(@PathVariable Long lineId) {
        lineDao.deleteById(lineId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/lines/{lineId}")
    public ResponseEntity<LineResponse> showLine(@PathVariable Long lineId) {
        Line getLine = lineDao.findById(lineId);
        return ResponseEntity.ok().body(new LineResponse(getLine.getId(), getLine.getName(), getLine.getColor(), null));
    }

    @PutMapping(value = "/lines/{lineId}")
    public ResponseEntity putLine(@PathVariable Long lineId, @RequestBody LineRequest lineRequest) {
        lineDao.update(new Line(lineId, lineRequest.getName(),lineRequest.getColor()));
        return ResponseEntity.ok().build();
    }

    public List<LineResponse> getLineResponses() {
        return lineDao.findAll().stream()
                .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor(), null))
                .collect(Collectors.toList());
    }

}

