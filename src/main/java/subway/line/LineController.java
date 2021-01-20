package subway.line;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.station.StationService;

import javax.annotation.Resource;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/lines")
public class LineController {

    @Resource
    private StationService stationService;
    @Resource
    private LineService lineService;
    @Resource
    private SectionService sectionService;

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        try {
            Line line = lineRequest.getLine();
            Long lindId = lineService.create(line);
            Section section = new Section(lindId, line);
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
        lineService.update(lineId, lineRequest);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{lineId}")
    public ResponseEntity deleteLine(@PathVariable Long lineId) {
        sectionService.delete(lineId);
        lineService.delete(lineId);

        return ResponseEntity.noContent().build();

    }

    @PostMapping(value = "/{lineId}/sections")
    public ResponseEntity createSection(@PathVariable Long lineId, @RequestBody SectionRequest sectionRequest) {
        sectionService.validateCreate(sectionRequest, stationService.getStations(lineId));
        sectionService.create(lineId, sectionRequest);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/{lineId}/sections")
    public ResponseEntity deleteSection(@PathVariable Long lineId, @RequestParam Long stationId) {
        sectionService.validateDelete(lineId);
        sectionService.delete(lineId, stationId);
        stationService.delete(stationId);

        return ResponseEntity.ok().build();
    }
}
