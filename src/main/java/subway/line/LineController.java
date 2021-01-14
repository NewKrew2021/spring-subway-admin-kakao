package subway.line;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.station.StationResponse;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class LineController {

    private final LineDao lineDao = new LineDao();

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        if (isNameDuplicate(lineRequest.getName())) {
            return ResponseEntity.badRequest().build();
        }

        Line line = new Line(lineRequest.getName(), lineRequest.getColor(), new ArrayList<>());
        Line newLine = lineDao.save(line);
        LineResponse lineResponse = new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor());

        return ResponseEntity.created(URI.create("/lines/" + newLine.getId()))
                .body(lineResponse);
    }

    private boolean isNameDuplicate(String name) {
        return lineDao.findByName(name) != null;
    }

    @GetMapping(value = "/lines/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        Line line = lineDao.findById(id);
        List<StationResponse> stationResponses = line.getStations().stream()
                .map(station -> new StationResponse(station.getId(), station.getName()))
                .collect(Collectors.toList());
        LineResponse lineResponse = new LineResponse(line.getId(), line.getName(), line.getColor(), stationResponses);

        return ResponseEntity.ok().body(lineResponse);
    }

    @GetMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<Line> lines = lineDao.findAll();
        List<LineResponse> lineResponses = lines.stream()
                .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor()))
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(lineResponses);
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        lineDao.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/lines/{id}")
    public ResponseEntity updateLine(@RequestBody LineRequest lineRequest, @PathVariable Long id) {
        Line line = lineDao.findById(id);
        Line updateLine = new Line(lineRequest.getName(), lineRequest.getColor(), line.getStations());
        lineDao.update(line, updateLine);
        return ResponseEntity.ok().build();
    }
}
