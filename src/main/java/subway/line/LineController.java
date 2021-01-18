package subway.line;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.exception.NotExistException;
import subway.section.SectionRequest;
import subway.section.SectionService;
import subway.station.StationResponse;

import java.net.URI;
import java.util.List;

@RestController
public class LineController {

    private final LineService lineService;
    private final SectionService sectionService;

    public LineController(LineService lineService, SectionService sectionService) {
        this.lineService = lineService;
        this.sectionService = sectionService;
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

    @RequestMapping(value = "/lines/{id}", method = RequestMethod.PUT)
    public ResponseEntity updateLine(@PathVariable long id, @RequestBody LineRequest lineRequest) {
        lineService.updateLine(id, lineRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        lineService.deleteLine(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/lines/{id}/sections")
    public ResponseEntity addSection(@PathVariable Long id, @RequestBody SectionRequest sectionRequest) {
        sectionService.addSection(id, sectionRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{id}/sections")
    public ResponseEntity deleteSection(@PathVariable Long id, @RequestParam Long stationId) {
        sectionService.deleteSection(id, stationId);
        return ResponseEntity.ok().build();
    }
}
