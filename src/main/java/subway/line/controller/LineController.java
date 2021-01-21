package subway.line.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.line.domain.Line;
import subway.line.domain.LineRequest;
import subway.line.domain.LineResponse;
import subway.line.service.LineService;
import subway.section.domain.SectionRequest;
import subway.section.domain.Sections;
import subway.section.service.SectionService;
import subway.station.domain.Stations;

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

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest request) {
        LineResponse response = lineService.insert(request);
        return ResponseEntity.created(URI.create("/lines/" + response.getId())).body(response);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<LineResponse> res = lineService.findAll();
        return ResponseEntity.ok(res);
    }

    @GetMapping(value = "/{lineId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLine(@PathVariable Long lineId) {
        Line line = lineService.findById(lineId);
        return ResponseEntity.ok(line.toDto(getStationsByLine(line)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        lineService.update(id, lineRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{lineId}")
    public ResponseEntity<?> deleteLine(@PathVariable Long lineId) {
        lineService.delete(lineId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{lineId}/sections")
    public ResponseEntity<?> addSection(@PathVariable Long lineId, @RequestBody SectionRequest request) {
        sectionService.insert(lineId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{lineId}/sections")
    public ResponseEntity<?> deleteSection(@PathVariable Long lineId, @RequestParam Long stationId) {
        sectionService.delete(lineId, stationId);
        return ResponseEntity.ok().build();
    }


    private Stations getStationsByLine(Line line) {
        Sections sections = sectionService.findByLineId(line.getId());
        return sectionService.getStations(sections);
    }
}

