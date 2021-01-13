package subway.line;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.station.StationResponse;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineDao lineDao;

    public LineController() {
        lineDao = new LineDao();
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Line newLine = lineDao.save(new Line(lineRequest.getName(), lineRequest.getColor()));
        LineResponse lineResponse = new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor());
        return ResponseEntity.created(URI.create("/lines/" + newLine.getId())).body(lineResponse);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> showLines() {
        return ResponseEntity.ok(lineDao.findAll()
                .stream()
                .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor()))
                .collect(Collectors.toList()));
    }

    @GetMapping("/{lineId}")
    public ResponseEntity<LineResponse> showLine(@PathVariable Long lineId) {
        Line newLine = lineDao.findOne(lineId);
        return ResponseEntity.ok(new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor()));
    }

    @PutMapping("/{lineId}")
    public ResponseEntity updateLine(@PathVariable Long lineId, @RequestBody LineRequest lineRequest) {
        lineDao.update(new Line(lineId, lineRequest.getName(), lineRequest.getColor()));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{lineId}")
    public ResponseEntity deleteLine(@PathVariable Long lineId) {
        lineDao.deleteById(lineId);
        return ResponseEntity.noContent().build();
    }
}
