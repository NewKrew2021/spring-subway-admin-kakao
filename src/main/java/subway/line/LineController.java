package subway.line;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.station.StationDao;
import subway.station.StationResponse;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class LineController {

    private final LineDao lineDao;
    private final StationDao stationDao;

    public LineController(LineDao lineDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
    }

    @PostMapping(value = "/lines", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {

        Line line = new Line(lineRequest);

        if (lineDao.hasContains(line)) {
            return ResponseEntity.badRequest().build();
        }
        lineDao.save(line);
        LineResponse lineResponse = new LineResponse(line.getId(), line.getName(), line.getColor(), null);

        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(lineResponse);
    }

    @GetMapping("/lines")
    public ResponseEntity<List<LineResponse>> showLines() {
        List<LineResponse> lineResponses = lineDao.getLines().stream()
                .map(line -> new LineResponse(line, null))
                .collect(Collectors.toList());
        return ResponseEntity.ok(lineResponses);
    }

    @GetMapping("/lines/{lineId}")
    public ResponseEntity<LineResponse> showLineById(@PathVariable long lineId) {
        Line line = lineDao.getLine(lineId); // 이 단계에서 station id는 line이 갖고 있으나, station 에 각각에 대한 정보는 없다.
        List<Long> stationsId = line.getStationsId();
        List<StationResponse> stationResponses = stationsId
                .stream()
                .map(stationDao::findById)
                .map(StationResponse::new)
                .collect(Collectors.toList());
        LineResponse lineResponse = new LineResponse(line, stationResponses); // 여기 lineResponse에 Line과 station 정보를 둘다 넣어줘야 한다.
        return ResponseEntity.ok(lineResponse);
    }

    @PutMapping("/lines/{id}")
    public ResponseEntity editLineById(@RequestBody LineRequest lineRequest, @PathVariable long id) {
        lineDao.editLineById(id, lineRequest.getName(), lineRequest.getColor());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity deleteLineById(@PathVariable long id) {
        lineDao.deleteLineById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/lines/{lineId}/sections")
    public ResponseEntity addSections(@RequestBody SectionRequest sectionRequest, @PathVariable long lineId) {
        Line line = lineDao.getLine(lineId);

        if (line.insertSection(sectionRequest)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @DeleteMapping(value = "/lines/{lineId}/sections")
    public ResponseEntity deleteSection(@PathVariable long lineId, @RequestParam long stationId) {
        Line line = lineDao.getLine(lineId);
        if (line.deleteSection(stationId)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

}
