package subway.line;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.exception.ExistLineSaveException;
import subway.section.*;
import subway.station.StationDao;
import subway.station.StationResponse;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class LineController {

    private final LineDao lineDao;
    private final StationDao stationDao;
    private LineService lineService;
    private SectionService sectionService;

    public LineController(LineDao lineDao, StationDao stationDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        lineService = new LineService(lineDao, sectionDao);
        sectionService = new SectionService(sectionDao);
    }

    @PostMapping(value = "/lines", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Line line = new Line(lineRequest);
        Long id;
        try {
            id = lineService.requestToLine(line);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
        sectionService.createTerminalSections(line, id);
        LineResponse lineResponse = new LineResponse(id, line.getName(), line.getColor(), null);
        return ResponseEntity.created(URI.create("/lines/" + id)).body(lineResponse);
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
        List<StationResponse> stationResponses = sectionService.getStationsIdOfLine(lineId)
                .stream()
                .map(stationDao::findById)
                .map(StationResponse::new)
                .collect(Collectors.toList());
        LineResponse lineResponse = new LineResponse(lineDao.getLine(lineId), stationResponses); // 여기 lineResponse에 Line과 station 정보를 둘다 넣어줘야 한다.
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
        SectionDto sectionDto = new SectionDto(sectionRequest, lineId);

        try {
            sectionService.insertSection( sectionDto );
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

    }

    @DeleteMapping(value = "/lines/{lineId}/sections")
    public ResponseEntity deleteSection(@PathVariable long lineId, @RequestParam long stationId) {
        SectionDto sectionDto = new SectionDto(lineId, stationId);
        try {
            sectionService.deleteSection(sectionDto);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

    }

}
