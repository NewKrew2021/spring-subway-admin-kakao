package subway.line;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        final Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        final Section section = new Section(lineRequest.getUpStationId(),
                lineRequest.getDownStationId(),
                lineRequest.getDistance());

        if (lineDao.isDuplicatedName(line)) {
            return ResponseEntity.badRequest().build();
        }

        Line newLine = lineDao.insert(line);
        sectionDao.insert(newLine.getId(), section);

        Sections sections = sectionDao.findByLineId(newLine.getId());
        Stations stations = new Stations(sections.getSortedStationIds().stream()
                .map(stationDao::findById)
                .collect(Collectors.toList()));

        LineResponse lineResponse = newLine.toDto(stations);
        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId())).body(lineResponse);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<LineResponse> res = lineDao.findAll()
                .stream()
                .map(line -> {
                    Sections sections = sectionDao.findByLineId(line.getId());
                    Stations stations = new Stations(sections.getSortedStationIds().stream()
                            .map(stationDao::findById)
                            .collect(Collectors.toList()));

                    return line.toDto(stations);
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(res);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        Line line = lineDao.findById(id);
        Sections sections = sectionDao.findByLineId(line.getId());
        Stations stations = new Stations(sections.getSortedStationIds().stream()
                .map(stationDao::findById)
                .collect(Collectors.toList()));

        return ResponseEntity.ok(line.toDto(stations));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LineResponse> updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        boolean updated = lineDao.update(id, lineRequest);
        if (!updated) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLine(@PathVariable Long id) {
        boolean deleted = lineDao.deleteById(id);
        if (!deleted) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{lineId}/sections")
    public ResponseEntity<LineResponse> addSection(@PathVariable Long lineId, @RequestBody SectionRequest sectionRequest) {
        final Section newSection = new Section(lineId,
                sectionRequest.getUpStationId(),
                sectionRequest.getDownStationId(),
                sectionRequest.getDistance());

        boolean created = sectionDao.insert(lineId, newSection);
        if (!created) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/sections")
    public ResponseEntity<LineResponse> deleteSection(@PathVariable Long id, @RequestParam Long stationId) {
        boolean deleted = sectionDao.delete(id, stationId);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok().build();
    }
}
