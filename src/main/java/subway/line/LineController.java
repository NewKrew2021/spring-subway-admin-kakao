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
        LineDto lineDto = new LineDto(lineRequest);

        if( lineDao.hasLineName(lineDto.getName()) ) {
            new ExistLineSaveException().printStackTrace();
            return ResponseEntity.badRequest().build();
        }

        Long id = lineService.requestToLine(lineDto);
        lineService.createTerminalSections(lineDto, id);

        LineResponse lineResponse = new LineResponse(id, lineDto.getName(), lineDto.getColor(), null);

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
        List<StationResponse> stationResponses = lineService.getStationsIdOfLine(lineId)
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
        SectionDto sectionDto = new SectionDto(sectionRequest);
        if (!sectionService.insertSection( sectionDto, lineId)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/lines/{lineId}/sections")
    public ResponseEntity deleteSection(@PathVariable long lineId, @RequestParam long stationId) {
        if (!sectionService.deleteSection(lineId, stationId)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok().build();
    }

}
