package subway.line.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.line.domain.LineRequest;
import subway.line.domain.LineResponse;
import subway.line.service.LineService;
import subway.section.domain.SectionRequest;
import subway.section.service.SectionService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/lines")
public class LineController {
    private final LineService lineService;
    private final SectionService sectionService;

    @Autowired
    public LineController(LineService lineService, SectionService sectionService) {
        this.lineService = lineService;
        this.sectionService = sectionService;
    }

    @DeleteMapping("/{lineId}/sections")
    public ResponseEntity<LineResponse> deleteLineSection(@PathVariable Long lineId, @RequestParam Long stationId) {
        return ResponseEntity.ok().body(sectionService.deleteSection(lineId, stationId));
    }


    @PostMapping("/{lineId}/sections")
    public ResponseEntity<LineResponse> createLineSection(@RequestBody SectionRequest sectionRequest, @PathVariable Long lineId) {
        return ResponseEntity.ok().body(lineService.addSectionToLine(lineId, sectionRequest));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> sectionHandle() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("구간 등록 실패");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> lineHandle() {
        return ResponseEntity.badRequest().body("노선 등록 실패");
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        LineResponse lineResponse = lineService.saveLineAndSection(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId()))
                .body(lineResponse);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        return ResponseEntity.ok().body(LineResponse.of(lineService.findById(id), lineService.getStations(id)));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        return ResponseEntity.ok().body(lineService.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        lineService.deleteLine(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<LineResponse> updateLine(@RequestBody LineRequest lineRequest, @PathVariable Long id) {
        lineService.updateLine(id, lineRequest);
        return ResponseEntity.ok().build();
    }
}
