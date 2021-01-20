package subway.line;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.section.Section;
import subway.section.SectionService;
import subway.station.StationResponse;
import subway.station.StationService;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/lines")
public class LineController {

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
        Line newLine = lineService.save(Line.of(lineRequest));
        sectionService.save(Section.of(newLine.getId(), lineRequest));

        return ResponseEntity.created(URI.create("/lines/" + newLine.getId()))
                .body(LineResponse.of(newLine));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        Line line = lineService.find(id);
        List<StationResponse> stationResponses = sectionService.getStationIds(id).stream()
                .map(stationId -> StationResponse.of(stationService.find(stationId)))
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(LineResponse.of(line, stationResponses));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<Line> lines = lineService.findAll();
        List<LineResponse> lineResponses = lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(lineResponses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        lineService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<LineResponse> updateLine(@RequestBody LineRequest lineRequest, @PathVariable Long id) {
        Line line = lineService.find(id);
        line.updateNameAndColor(lineRequest.getName(), lineRequest.getColor());
        lineService.update(line);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity exceptionHandler(Exception e) {
        e.printStackTrace();

        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
