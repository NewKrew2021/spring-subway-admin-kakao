package subway.line;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.section.Section;
import subway.section.SectionRequest;
import subway.section.SectionService;
import subway.station.StationDao;
import subway.station.StationResponse;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/lines")
public class LineController {
    private final StationDao stationDao;
    private final LineDao lineDao;
    private final SectionService sectionService;

    public LineController(StationDao stationDao, LineDao lineDao, SectionService sectionService) {
        this.stationDao = stationDao;
        this.lineDao = lineDao;
        this.sectionService = sectionService;
    }

    @DeleteMapping("/{lineId}/sections")
    public ResponseEntity<LineResponse> deleteLineSection(@PathVariable Long lineId, @RequestParam Long stationId) {
        Line line = lineDao.findById(lineId);
        sectionService.deleteSection(line.getId(), stationId);

        return ResponseEntity.ok().body(LineResponse.of(line));
    }


    @PostMapping("/{lineId}/sections")
    public ResponseEntity<LineResponse> createLineSection(@RequestBody SectionRequest sectionRequest, @PathVariable Long lineId) {
        sectionService.addSectionToLine(Section.of(lineId, sectionRequest));
        return ResponseEntity.ok().body(LineResponse.of(lineDao.findById(lineId)));
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        if (isNameDuplicate(lineRequest.getName())) {
            return ResponseEntity.badRequest().build();
        }

        Line newLine = lineDao.save(Line.of(lineRequest));
        sectionService.save(Section.of(newLine.getId(), lineRequest));

        return ResponseEntity.created(URI.create("/lines/" + newLine.getId()))
                .body(LineResponse.of(newLine));
    }

    private boolean isNameDuplicate(String name) {
        return lineDao.findByName(name) != null;
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        Line line = lineDao.findById(id);
        List<StationResponse> stationResponses = sectionService.getStationIds(id).stream()
                .map(stationId -> StationResponse.of(stationDao.findById(stationId)))
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(LineResponse.of(line, stationResponses));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<Line> lines = lineDao.findAll();
        List<LineResponse> lineResponses = lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(lineResponses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        lineDao.deleteById(id);
        sectionService.deleteLineId(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<LineResponse> updateLine(@RequestBody LineRequest lineRequest, @PathVariable Long id) {
        Line line = lineDao.findById(id);
        line.updateNameAndColor(lineRequest.getName(), lineRequest.getColor());
        lineDao.update(line);
        return ResponseEntity.ok().build();
    }
}
