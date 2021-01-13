package subway.line;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.station.Station;
import subway.station.StationResponse;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class LineController {

    private LineDao lineDao;

    public LineController(){
        this.lineDao = new LineDao();
    }

    @PostMapping(value = "/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(),lineRequest.getColor());
        Line newLine = lineDao.save(line);
        LineResponse lineResponse = new LineResponse(newLine.getId(),newLine.getName(),newLine.getColor());
        return ResponseEntity.created(URI.create("/lines/" + newLine.getId())).body(lineResponse);
    }

    @GetMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<Line> lines = lineDao.findAll();
        List<LineResponse> lineResponses = lines.stream().map(LineResponse::new).collect(Collectors.toList());
        return ResponseEntity.ok().body(lineResponses);
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        lineDao.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/lines/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        Optional<Line> lineOptional = lineDao.findById(id);
        LineResponse lineResponse = new LineResponse(lineOptional.get());
        return ResponseEntity.ok(lineResponse);
    }

}
