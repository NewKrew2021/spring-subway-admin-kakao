package subway.line;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.section.Section;
import subway.section.SectionDao;
import subway.section.SectionRequest;
import subway.section.Sections;
import subway.station.StationDao;
import subway.station.Stations;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/lines")
public class LineController {
    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public LineController(LineDao lineDao, SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest request) {
        final Line line = new Line(request.getName(), request.getColor());

        if (lineDao.isDuplicatedName(line)) {
            return ResponseEntity.badRequest().build();
        }
        Line newLine = lineDao.insert(line);
        if (newLine == null) {
            return ResponseEntity.badRequest().build();
        }

        boolean created = sectionDao.insertOnCreateLine(newLine.getId(), request);
        if (!created) {
            return ResponseEntity.badRequest().build();
        }

        LineResponse lineResponse = newLine.toDto(getStationsByLine(newLine));
        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId())).body(lineResponse);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<LineResponse> res = lineDao.findAll()
                .stream()
                .map(line -> line.toDto(getStationsByLine(line)))
                .collect(Collectors.toList());

        return ResponseEntity.ok(res);
    }

    @GetMapping(value = "/{lineId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLine(@PathVariable Long lineId) {
        Line line = lineDao.findById(lineId);
        return ResponseEntity.ok(line.toDto(getStationsByLine(line)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        boolean updated = lineDao.update(id, lineRequest);
        if (!updated) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{lineId}")
    public ResponseEntity<?> deleteLine(@PathVariable Long lineId) {
        boolean deleted = lineDao.delete(lineId);
        if (!deleted) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{lineId}/sections")
    public ResponseEntity<?> addSection(@PathVariable Long lineId, @RequestBody SectionRequest request) {
        boolean created = sectionDao.insert(lineId, request);
        if (!created) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{lineId}/sections")
    public ResponseEntity<?> deleteSection(@PathVariable Long lineId, @RequestParam Long stationId) {
        final Section section = new Section(lineId, stationId, 0);

        boolean deleted = sectionDao.delete(section);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok().build();
    }

    private Stations getStationsByLine(Line line) {
        Sections sections = sectionDao.findByLineId(line.getId());
        return new Stations(sections.getStationIds().stream()
                .map(stationDao::findById)
                .collect(Collectors.toList()));
    }
}
