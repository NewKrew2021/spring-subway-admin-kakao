package subway.line;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
public class LineController {

    private final LineDao lineDao;

    public LineController() {
        this.lineDao = new LineDao();
    }

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        if (lineDao.existName(lineRequest.getName())) {
            return ResponseEntity.badRequest().build();
        }
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        Line newLine = this.lineDao.save(line);
        LineResponse lineResponse = new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor(), null);
        return ResponseEntity.created(URI.create("/lines/" + newLine.getId())).body(lineResponse);
    }

    @GetMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<Line> lines = lineDao.findAll();
        List<LineResponse> lineResponses = new ArrayList<>();
        for (Line line : lines) {
            lineResponses.add(new LineResponse(line.getId(), line.getName(), line.getColor(), null));
        }
        return ResponseEntity.ok().body(lineResponses);
    }

//    @GetMapping(value = "/lines/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<List<LineResponse>> showLines(@PathVariable long id) {
//        Line line = lineDao.findById(id);
//        List<LineResponse> lineResponses = new ArrayList<>();
//        for (Line line : lines) {
//            lineResponses.add(new LineResponse(line.getId(), line.getName(), line.getColor(), null));
//        }
//        return ResponseEntity.ok().body(lineResponses);
//    }

//    @RequestMapping(value = "/lines/{id}", method = RequestMethod.PUT)
//    public ResponseEntity updateLine(@PathVariable long id, @RequestBody LineRequest lineRequest) {
//
//        return ResponseEntity.ok().body(lineResponses);
//    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        this.lineDao.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
