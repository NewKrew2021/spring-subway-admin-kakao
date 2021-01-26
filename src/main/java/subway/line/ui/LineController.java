package subway.line.ui;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.line.factory.LineFactory;
import subway.line.application.LineService;
import subway.line.dto.LineRequest;
import subway.line.dto.LineResponse;
import subway.section.application.SectionService;
import subway.section.dao.SectionDao;
import subway.section.dto.SectionRequest;

import java.net.URI;
import java.util.List;

@RestController
public class LineController {

    private final LineService lineService;
    private final SectionService sectionService;
    private final SectionDao sectionDao;

    public LineController(LineService lineService, SectionService sectionService, SectionDao sectionDao) {
        this.lineService = lineService;
        this.sectionService = sectionService;
        this.sectionDao = sectionDao;
    }

    @PostMapping(value = "/lines", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        LineResponse lineResponse = lineService.insertLine(lineRequest);

        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId())).body(lineResponse);
    }

    @GetMapping("/lines")
    public ResponseEntity<List<LineResponse>> showLines() {
        List<LineResponse> lineResponses = lineService.findAll();
        return ResponseEntity.ok(lineResponses);
    }

    @GetMapping("/lines/{lineId}")
    public ResponseEntity<LineResponse> showLineById(@PathVariable long lineId) {
        LineResponse lineResponse = lineService.findById(lineId);
        return ResponseEntity.ok(lineResponse);
    }

    @PutMapping("/lines/{id}")
    public ResponseEntity<Void> editLineById(@RequestBody LineRequest lineRequest, @PathVariable long id) {
        lineService.update(LineFactory.makeLine(lineRequest));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity<Void> deleteLineById(@PathVariable long id) {
        lineService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/lines/{lineId}/sections")
    public ResponseEntity<Void> addSections(@RequestBody SectionRequest sectionRequest, @PathVariable long lineId) {
        return sectionService.insertSection(sectionRequest, lineId);
    }

    @DeleteMapping(value = "/lines/{lineId}/sections")
    public ResponseEntity<Void> deleteSection(@PathVariable long lineId, @RequestParam long stationId) {
        return sectionService.deleteSection(lineId, stationId);
    }

}
