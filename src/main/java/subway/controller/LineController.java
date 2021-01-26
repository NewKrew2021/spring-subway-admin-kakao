package subway.controller;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.controller.dto.LineRequest;
import subway.controller.dto.LineResponse;
import subway.controller.dto.SectionRequest;
import subway.domain.Line;
import subway.domain.Section;
import subway.service.LineService;
import subway.service.SectionService;
import subway.service.StationService;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/lines")
public class LineController {
    private static final boolean FIRST_SECTION = true;
    private static final boolean LAST_SECTION = true;
    private final StationService stationService;
    private final LineService lineService;
    private final SectionService sectionService;

    public LineController(StationService stationService, LineService lineService, SectionService sectionService) {
        this.stationService = stationService;
        this.lineService = lineService;
        this.sectionService = sectionService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        try {
            Line line = lineRequest.getLine();
            Long lindId = lineService.create(line);
            Section section = new Section(lindId, lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance(), FIRST_SECTION, LAST_SECTION);
            sectionService.create(section);
            LineResponse lineResponse = new LineResponse(lindId, line.getName(), line.getColor(), stationService.getStations(lindId));

            return ResponseEntity.created(URI.create("/lines/" + lindId)).body(lineResponse);
        } catch (DuplicateKeyException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<Line> lines = lineService.getLines();
        List<LineResponse> lineResponses = lines.stream()
                .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor(), stationService.getStations(line.getId())))
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping(value = "/{lineId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLine(@PathVariable Long lineId) {
        Line line = lineService.getLine(lineId);
        LineResponse lineResponse = new LineResponse(lineId, line.getName(), line.getColor(), stationService.getStations(lineId));

        return ResponseEntity.ok(lineResponse);
    }

    @PutMapping(value = "/{lineId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateLine(@PathVariable Long lineId, @RequestBody LineRequest lineRequest) {
        Line line = lineRequest.getLine(lineId);
        lineService.update(line);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{lineId}")
    public ResponseEntity deleteLine(@PathVariable Long lineId) {
        lineService.delete(lineId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{lineId}/sections")
    public ResponseEntity createSection(@PathVariable Long lineId, @RequestBody SectionRequest sectionRequest) {
        Section section = sectionRequest.getSection(lineId);
        sectionService.create(lineId, section);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/{lineId}/sections")
    public ResponseEntity deleteSection(@PathVariable Long lineId, @RequestParam Long stationId) {
        sectionService.delete(lineId, stationId);

        return ResponseEntity.ok().build();
    }
}
