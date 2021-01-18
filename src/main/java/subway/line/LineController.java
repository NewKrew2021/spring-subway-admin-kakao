package subway.line;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.station.Station;
import subway.station.StationDao;
import subway.station.Stations;

import java.net.URI;
import java.util.ArrayList;
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
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        Section section = new Section(line.getId(),
                lineRequest.getUpStationId(),
                lineRequest.getDownStationId(),
                lineRequest.getDistance());

        if (lineDao.isDuplicatedName(line)) {
            return ResponseEntity.badRequest().build();
        }

        Line newLine = lineDao.insert(line);
        sectionDao.insertDirectly(newLine.getId(), section);

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

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        Line line = lineDao.findById(id);
        return ResponseEntity.ok(line.toDto(getStationsByLine(line)));
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
        boolean isDone = sectionDao.insert(lineId, sectionRequest);
        if (!isDone) {
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

    private Stations getStationsByLine(Line line) {
        List<Station> stations = new ArrayList<>();

        Long lineId = line.getId();
        Section firstSection = sectionDao.findFirstStation2(lineId);
        Long firstStationId = firstSection.getUpStationId();

        stations.add(stationDao.findById(firstStationId));
        Long stationId = firstStationId;
        for (int i = 0; i < sectionDao.findByLineId(lineId).size(); i++) {
            Section section = sectionDao.findByUpStationId(lineId, stationId);
            stations.add(stationDao.findById(section.getDownStationId()));
            stationId = section.getDownStationId();
        }

        return new Stations(stations);
    }
}
