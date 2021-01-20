package subway.line;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.section.Section;
import subway.section.SectionDao;
import subway.section.SectionRequest;
import subway.section.Sections;
import subway.station.StationDao;
import subway.station.domain.Station;
import subway.station.domain.Stations;

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

        final Line newLine = lineDao.insert(line);

        final Section upSection = new Section(newLine.getID(), request.getUpStationID(), 0);
        final Section downSection = new Section(newLine.getID(), request.getDownStationID(), request.getDistance());
        sectionDao.insert(upSection, downSection);

        LineResponse lineResponse = newLine.toResultValue(getStationsByLine(newLine));
        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getID())).body(lineResponse);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<LineResponse> res = lineDao.findAll()
                .stream()
                .map(line -> line.toResultValue(getStationsByLine(line)))
                .collect(Collectors.toList());

        return ResponseEntity.ok(res);
    }

    @GetMapping(value = "/{lineID}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLine(@PathVariable Long lineID) {
        Line line = lineDao.findOne(lineID);
        return ResponseEntity.ok(line.toResultValue(getStationsByLine(line)));
    }

    @PutMapping("/{lineID}")
    public ResponseEntity<LineResponse> updateLine(@PathVariable Long lineID, @RequestBody LineRequest lineRequest) {
        lineDao.update(lineID, lineRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{lineID}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long lineID) {
        lineDao.delete(lineID);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{lineID}/sections")
    public ResponseEntity<LineResponse> addSection(@PathVariable Long lineID, @RequestBody SectionRequest request) {
        final Section upSection = new Section(lineID, request.getUpStationID(), 0);
        final Section downSection = new Section(lineID, request.getDownStationID(), request.getDistance());

        sectionDao.insert(upSection, downSection);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{lineID}/sections")
    public ResponseEntity<Void> deleteSection(@PathVariable Long lineID, @RequestParam Long stationID) {
        final Section section = new Section(lineID, stationID, 0);

        sectionDao.delete(section);
        return ResponseEntity.ok().build();
    }

    private Stations getStationsByLine(Line line) {
        Sections sections = sectionDao.findAllSectionsOf(line.getID());
        return new Stations(sections.getStationIDs().stream()
                .map(id -> new Station(id, "unused"))
                .map(stationDao::findByID)
                .collect(Collectors.toList()));
    }
}
