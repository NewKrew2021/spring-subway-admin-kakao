package subway.line.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.line.domain.LineRequest;
import subway.line.domain.LineResponse;
import subway.line.service.LineService;
import subway.section.domain.SectionRequest;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineService lineService;

    @Autowired
    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        LineResponse response = null;

        try {
            response = lineService.createLine(lineRequest);
        } catch (DuplicateKeyException dke) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity
                .created(URI.create("/lines/" + response.getId()))
                .body(response);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        return ResponseEntity
                .ok()
                .body(lineService.showLines());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        lineService.deleteLine(id);
        return ResponseEntity
                .noContent()
                .build();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        return ResponseEntity
                .ok(lineService.showLine(id));
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateLine(@PathVariable Long id,
                                     @RequestBody LineRequest lineRequest) {
        lineService.updateLine(id, lineRequest);
        return ResponseEntity
                .ok()
                .build();
    }

    @PostMapping(value = "/{id}/sections")
    public ResponseEntity createSection(@PathVariable Long id,
                                        @RequestBody SectionRequest sectionRequest) {
        try {
            lineService.createSection(id, sectionRequest);
        } catch (IllegalArgumentException iae) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity
                .ok()
                .build();
    }

    @DeleteMapping(value = "/{id}/sections")
    public ResponseEntity deleteSection(@PathVariable Long id,
                                        @RequestParam Long stationId) {
        lineService.deleteSection(id, stationId);
        return ResponseEntity
                .ok()
                .build();
    }
}
