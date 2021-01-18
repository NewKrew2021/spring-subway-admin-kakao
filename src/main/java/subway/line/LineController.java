package subway.line;

import org.springframework.dao.IncorrectUpdateSemanticsDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.exception.LineNotFoundException;
import subway.section.SectionRequest;
import subway.section.SectionService;
import subway.section.Sections;
import subway.station.StationDao;
import subway.station.StationResponse;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class LineController {
    private final LineDao lineDao;
    private final SectionService sectionService;

    public LineController(LineDao lineDao, SectionService sectionService) {
        this.lineDao = lineDao;
        this.sectionService = sectionService;
    }

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        if (lineDao.existBy(lineRequest.getName())) {
            throw new IllegalArgumentException("이미 등록된 지하철 노선 입니다.");
        }

        Line newLine = lineDao.save(new Line(lineRequest.getName(), lineRequest.getColor()));

        sectionService.save(Sections.createInitialSections(lineRequest.getSectionRequest().toEntity(newLine.getId())));

        return ResponseEntity.created(URI.create("/lines/" + newLine.getId()))
                .body(new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor(), Collections.emptyList()));
    }

    @GetMapping(value = "/lines/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        Line line = lineDao.findById(id).orElseThrow(() -> new LineNotFoundException(id));
        List<StationResponse> stationResponses = sectionService.findStationsOf(line.getId());
        return ResponseEntity.ok(new LineResponse(line.getId(), line.getName(), line.getColor(), stationResponses));
    }

    @GetMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        return lineDao.findAll()
                .stream()
                .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor(), Collections.emptyList()))
                .collect(Collectors.collectingAndThen(Collectors.toList(), ResponseEntity::ok));
    }

    @PutMapping(value = "/lines/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateLine(@RequestBody LineRequest lineRequest, @PathVariable Long id) {
        Line line = new Line(id, lineRequest.getName(), lineRequest.getColor());
        try {
            lineDao.update(line);
        } catch (IncorrectUpdateSemanticsDataAccessException e) {
            throw new LineNotFoundException(line.getId());
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineDao.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/lines/{lineId}/sections")
    public ResponseEntity<Void> createSection(@PathVariable Long lineId, @RequestBody SectionRequest request) {
        sectionService.createSection(lineId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{lineId}/sections")
    public ResponseEntity<Void> deleteSections(@PathVariable Long lineId, @RequestParam Long stationId) {
        sectionService.removeSection(lineId, stationId);
        return ResponseEntity.ok().build();
    }
}
