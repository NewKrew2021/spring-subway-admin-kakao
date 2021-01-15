package subway.line;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static subway.Container.*;

@RestController
public class LineController {

    private final LineService lineService;
    private final SectionService sectionService;

    public LineController() {
        this.lineService = new LineService();
        this.sectionService = new SectionService();
    }

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        if (lineService.existName(lineRequest.getName())) {
            return ResponseEntity.badRequest().build();
        }

        try {
            LineResponse lineResponse = lineService.createLine(lineRequest);
            return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId())).body(lineResponse);
        } catch (NotExistException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<LineResponse> lineResponses = lineService.getAllLines();
        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping(value = "/lines/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLines(@PathVariable long id) {
        Line line = lineService.getLine(id);
        List<StationResponse> stations = sectionService.getStationsOfLine(line);
        LineResponse lineResponse = new LineResponse(line.getId(), line.getName(), line.getColor(), stations);
        return ResponseEntity.ok().body(lineResponse);
    }

//    @RequestMapping(value = "/lines/{id}", method = RequestMethod.PUT)
//    public ResponseEntity updateLine(@PathVariable long id, @RequestBody LineRequest lineRequest) {
//
//        return ResponseEntity.ok().body(lineResponses);
//    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        lineService.deleteLine(id);
        return ResponseEntity.noContent().build();
    }
}
