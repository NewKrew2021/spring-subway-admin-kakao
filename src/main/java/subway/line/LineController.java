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
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest request) {
        final Line line = new Line(request.getName(), request.getColor());

        if (lineDao.isDuplicatedName(line)) {
            return ResponseEntity.badRequest().build();
        }
        final Line newLine = lineDao.insert(line);

        final Section upSection = new Section(newLine.getID(), request.getUpStationID(), 0);
        final Section downSection = new Section(newLine.getID(), request.getDownStationID(), request.getDistance());

        boolean created = sectionDao.insert(upSection, downSection);
        if (!created) {
            return ResponseEntity.badRequest().build();
        }

        LineResponse lineResponse = newLine.toDto(getStationsByLine(newLine));
        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getID())).body(lineResponse);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<LineResponse> res = lineDao.findAll()
                .stream()
                .map(line -> line.toDto(getStationsByLine(line)))
                .collect(Collectors.toList());

        return ResponseEntity.ok(res);
    }

    @GetMapping(value = "/{lineID}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLine(@PathVariable Long lineID) {
        Line line = lineDao.findOne(lineID);
        return ResponseEntity.ok(line.toDto(getStationsByLine(line)));
    }

    @PutMapping("/{lineID}")
    public ResponseEntity<?> updateLine(@PathVariable Long lineID, @RequestBody LineRequest lineRequest) {
        boolean updated = lineDao.update(lineID, lineRequest);
        if (!updated) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{lineID}")
    public ResponseEntity<?> deleteLine(@PathVariable Long lineID) {
        boolean deleted = lineDao.delete(lineID);
        if (!deleted) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{lineID}/sections")
    public ResponseEntity<?> addSection(@PathVariable Long lineID, @RequestBody SectionRequest request) {
        final Section upSection = new Section(lineID, request.getUpStationID(), 0);
        final Section downSection = new Section(lineID, request.getDownStationID(), request.getDistance());

        boolean created = sectionDao.insert(upSection, downSection);
        if (!created) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{lineID}/sections")
    public ResponseEntity<?> deleteSection(@PathVariable Long lineID, @RequestParam Long stationID) {
        final Section section = new Section(lineID, stationID, 0);

        boolean deleted = sectionDao.delete(section);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok().build();
    }

    private Stations getStationsByLine(Line line) {
        Sections sections = sectionDao.findAllSectionsOf(line.getID());
        return new Stations(sections.getStationIDs().stream()
                .map(stationDao::findByID)
                .collect(Collectors.toList()));
    }
}
