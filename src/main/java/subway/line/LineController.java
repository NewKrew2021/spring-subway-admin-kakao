package subway.line;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class LineController {

    private static LineDao lineDao = new LineDao();

    @PostMapping(value = "/lines", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {

        Line line = new Line(lineRequest);

        if (lineDao.hasContains(line)) {
            return ResponseEntity.badRequest().build();
        }
        lineDao.save(line);
        LineResponse lineResponse = new LineResponse(line.getId(), line.getName(), line.getColor(), null);

        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(lineResponse);
    }

    @GetMapping("/lines")
    public ResponseEntity<List<LineResponse>> showLines() {
        List<LineResponse> lineResponses = lineDao.getLines().stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lineResponses);
    }

    @GetMapping("/lines/{id}")
    public ResponseEntity<LineResponse> showLineById(@PathVariable long id) {
        LineResponse lineResponse = new LineResponse(lineDao.getLine(id));
        return ResponseEntity.ok(lineResponse);
    }

    @PutMapping("/lines/{id}")
    public ResponseEntity editLineById(@RequestBody LineRequest lineRequest, @PathVariable long id) {
        lineDao.editLineById(id, lineRequest.getName(), lineRequest.getColor());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity deleteLineById(@PathVariable long id) {
        lineDao.deleteLineById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/lines/{lineId}/sections")
    public ResponseEntity addSections(@RequestBody SectionRequest sectionRequest, @PathVariable long lineId) {
        Line line = lineDao.getLine(lineId);

        if (line.insertSection(sectionRequest)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @DeleteMapping(value = "/lines/{lineId}/sections")
    public ResponseEntity deleteSection(@PathVariable long lineId ,@RequestParam long stationId) {
        Line line = lineDao.getLine(lineId);
        if ( line.deleteSection(stationId) ) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

}
