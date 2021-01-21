package subway.section;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.line.Line;
import subway.line.LineResponse;
import subway.line.LineService;

@RestController
@RequestMapping("/lines/{lineId}")
public class SectionController {

    LineService lineService;
    SectionService sectionService;

    public SectionController(LineService lineService, SectionService sectionService) {
        this.lineService = lineService;
        this.sectionService = sectionService;
    }

    @PostMapping("/sections")
    public ResponseEntity<LineResponse> createLineSection(@RequestBody SectionRequest sectionRequest, @PathVariable Long lineId) {
        sectionService.addSectionToLine(sectionRequest.toDomainObject(lineId));
        return ResponseEntity.ok().body(LineResponse.of(lineService.find(lineId)));
    }

    @DeleteMapping("/sections")
    public ResponseEntity<LineResponse> deleteLineSection(@PathVariable Long lineId, @RequestParam Long stationId) {
        Line line = lineService.find(lineId);
        sectionService.deleteSection(line.getId(), stationId);

        return ResponseEntity.ok().body(LineResponse.of(line));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity exceptionHandler(Exception e) {
        e.printStackTrace();

        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
