package subway.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.dao.StationDao;
import subway.domain.Line;
import subway.domain.Section;
import subway.dto.LineRequest;
import subway.dto.LineResponse;
import subway.dto.SectionRequest;
import subway.service.LineService;
import subway.service.SectionService;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/lines")
public class LineController {
    private final StationDao stationDao;
    private final LineService lineService;
    private final SectionService sectionService;

    public LineController(StationDao stationDao, LineService lineService, SectionService sectionService) {
        this.stationDao = stationDao;
        this.lineService = lineService;
        this.sectionService = sectionService;
    }

    @DeleteMapping("/{lineId}/sections")
    public ResponseEntity<LineResponse> deleteLineSection(@PathVariable Long lineId, @RequestParam Long stationId) {
        Line line = lineService.findById(lineId);
        sectionService.deleteSection(line.getId(), stationId);
        return ResponseEntity.ok().body(LineResponse.of(line));
    }


    @PostMapping("/{lineId}/sections")
    public ResponseEntity<LineResponse> createLineSection(@RequestBody SectionRequest sectionRequest, @PathVariable Long lineId) {
        sectionService.addSectionToLine(Section.of(lineId, sectionRequest));
        return ResponseEntity.ok().body(LineResponse.of(lineService.findById(lineId)));
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        if (lineService.isDuplicateName(lineRequest.getName())) {
            return ResponseEntity.badRequest().build();
        }

        Line newLine = lineService.save(Line.of(lineRequest));
        sectionService.save(Section.of(newLine.getId(), lineRequest));

        return ResponseEntity.created(URI.create("/lines/" + newLine.getId()))
                .body(LineResponse.of(newLine));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        return ResponseEntity.ok().body(LineResponse.of(lineService.findById(id), sectionService.getStationsById(id)));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        return ResponseEntity.ok().body(lineService.findAll()
                .stream()
                .map(LineResponse::of)
                .collect(Collectors.toList()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        lineService.deleteById(id);
        sectionService.deleteLineId(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<LineResponse> updateLine(@RequestBody LineRequest lineRequest, @PathVariable Long id) {
        lineService.update(id, lineRequest);
        return ResponseEntity.ok().build();
    }
}
