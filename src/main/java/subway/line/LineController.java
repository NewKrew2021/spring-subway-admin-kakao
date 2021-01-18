package subway.line;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class LineController {

    private LineService lineService;

    public LineController(){
        this.lineService = new LineService();
    }

    @PostMapping(value = "/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        return lineService.createLine(lineRequest);
    }

    @GetMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        return lineService.showLines();
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        return lineService.deleteLine(id);
    }

    @GetMapping(value = "/lines/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        return lineService.showLine(id);
    }

    @PutMapping(value = "/lines/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        return lineService.updateLine(id, lineRequest);
    }

    @PostMapping(value = "/lines/{id}/sections")
    public ResponseEntity createSection(@PathVariable Long id, @RequestBody SectionRequest sectionRequest) {
        return lineService.createSection(id, sectionRequest);
    }

    @DeleteMapping(value = "/lines/{id}/sections")
    public ResponseEntity deleteSection(@PathVariable Long id, @RequestParam Long stationId) {
        return lineService.deleteSection(id, stationId);
    }
}
