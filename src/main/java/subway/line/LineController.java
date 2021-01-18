package subway.line;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.SubwayApplication;
import subway.station.Station;
import subway.station.StationController;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/lines")
public class LineController {
    private final int LAST_STATION_HAS_NO_DISTANCE = 0;
    private final LineDao lineDao;
    private final StationController stationController;

    public LineController(LineDao lineDao, StationController stationController) {
        this.lineDao = lineDao;
        this.stationController = stationController;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SubwayApplication.class);
        Station upStation = stationController.getStationDao().findById(lineRequest.getUpStationId());
        Station downStation = stationController.getStationDao().findById(lineRequest.getDownStationId());

        List<Section> sections = Arrays.asList(
                new Section(upStation, lineRequest.getDistance()),
                new Section(downStation, LAST_STATION_HAS_NO_DISTANCE));
        Line line = new Line(lineRequest.getName(), lineRequest.getColor(), lineRequest.getExtraFare(), sections);

        Line newLine = lineDao.insert(line);
        if (newLine == null) {
            return ResponseEntity.badRequest().build();
        }

        LineResponse lineResponse = newLine.toDto();
        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId())).body(lineResponse);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<LineResponse> res = lineDao.findAll()
                .stream()
                .map(Line::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(res);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        return ResponseEntity.ok(lineDao.findById(id).toDto());
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
        Line line = lineDao.findById(lineId);
        Station upStation = stationController.getStationDao().findById(sectionRequest.getUpStationId());
        Station downStation = stationController.getStationDao().findById(sectionRequest.getDownStationId());
        int distance = sectionRequest.getDistance();

        boolean isDone = line.insertSection(upStation, downStation, distance);
        if (!isDone) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok(line.toDto());
    }

    @DeleteMapping("/{id}/sections")
    public ResponseEntity<LineResponse> deleteSection(@PathVariable Long id, @RequestParam Long stationId) {
        Line line = lineDao.findById(id);

        boolean isDone = line.deleteById(stationId);
        if (!isDone) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok().build();
    }
}
