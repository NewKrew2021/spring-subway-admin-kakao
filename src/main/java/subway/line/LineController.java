package subway.line;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    public LineController() {
        this.lineDao = new LineDao();
    }

    @Autowired
    StationController stationController;

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Station upStation = stationController.getStationDao().findOne(lineRequest.getUpStationId());
        Station downStation = stationController.getStationDao().findOne(lineRequest.getDownStationId());
        List<Section> sections = Arrays.asList(
                new Section(upStation, lineRequest.getDistance()),
                new Section(downStation, LAST_STATION_HAS_NO_DISTANCE));
        Line line = new Line(lineRequest.getName(), lineRequest.getColor(), lineRequest.getExtraFare(), sections);

        Line newLine = lineDao.save(line);
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
        return ResponseEntity.ok(lineDao.findOne(id).toDto());
    }

    @PutMapping("/{id}")
    public ResponseEntity<LineResponse> updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        Line updatedLine = lineDao.update(id, lineRequest);
        if (updatedLine == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(updatedLine.toDto());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLine(@PathVariable Long id) {
        lineDao.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{lineId}/sections")
    public ResponseEntity<LineResponse> addSection(@PathVariable Long lineId, @RequestBody SectionRequest sectionRequest) {
        Line line = lineDao.findOne(lineId);
        Station upStation = stationController.getStationDao().findOne(sectionRequest.getUpStationId());
        Station downStation = stationController.getStationDao().findOne(sectionRequest.getDownStationId());
        int distance = sectionRequest.getDistance();

        boolean isDone = line.insertSection(upStation, downStation, distance);
        if (!isDone) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok(line.toDto());
    }

    @DeleteMapping("/{id}/sections")
    public ResponseEntity<LineResponse> deleteSection(@PathVariable Long id, @RequestParam Long stationId) {
        Line line = lineDao.findOne(id);

        boolean isDone = line.deleteById(stationId);
        if (!isDone) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok().build();
    }
}
