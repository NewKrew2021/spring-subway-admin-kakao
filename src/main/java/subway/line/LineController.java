package subway.line;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.station.StationResponse;

import java.net.URI;
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

//    @GetMapping
//    public ResponseEntity<List<LineResponse>> showLines() {
//
//    }
//
//    @GetMapping("/{lineId}")
//    public ResponseEntity<LineResponse> showLine(@PathVariable Long lineId) {
//
//    }
//
//    @PutMapping("/{lineId}")
//    public ResponseEntity updateLine(@PathVariable Long lineId) {
//
//    }
//
    @DeleteMapping("/{lineId}")
    public ResponseEntity deleteLine(@PathVariable Long lineId) {
        lineDao.deleteById(lineId);
        return ResponseEntity.noContent().build();
    }
}
