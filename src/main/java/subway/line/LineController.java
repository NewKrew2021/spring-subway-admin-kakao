package subway.line;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.section.Section;
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

    @Autowired
    public LineController(StationDao stationDao, LineDao lineDao, SectionService sectionService) {
        this.stationDao = stationDao;
        this.lineDao = lineDao;
        this.sectionService = sectionService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Line newLine = lineDao.save(Line.of(lineRequest));
        sectionService.save(Section.of(newLine.getId(), lineRequest));

        return ResponseEntity.created(URI.create("/lines/" + newLine.getId()))
                .body(LineResponse.of(newLine));
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
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<LineResponse> updateLine(@RequestBody LineRequest lineRequest, @PathVariable Long id) {
        Line line = lineDao.findById(id);
        line.updateNameAndColor(lineRequest.getName(), lineRequest.getColor());
        lineDao.update(line);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity exceptionHandler(Exception exception) {
        exception.printStackTrace();

        if (exception instanceof DuplicateKeyException) {
            return ResponseEntity.badRequest().body("요청한 이름의 Line이 이미 존재합니다.");
        }

        return ResponseEntity.badRequest().build();
    }
}
