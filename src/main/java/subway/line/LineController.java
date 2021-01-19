package subway.line;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.section.SectionDao;
import subway.section.SectionDto;
import subway.section.SectionRequest;
import subway.station.StationDao;
import subway.station.StationResponse;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class LineController {

    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;
    private LineService lineService;

    public LineController(LineDao lineDao, StationDao stationDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
        lineService = new LineService(lineDao, sectionDao);
    }

    @PostMapping(value = "/lines", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        if( lineDao.hasContainLine(lineRequest.getName()) ) {
            return ResponseEntity.badRequest().build();
        }

        Long id = lineService.requestToLine(lineRequest.getName(), lineRequest.getColor());
        lineService.createTerminalSections(lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance(), id);

        LineResponse lineResponse = new LineResponse(id, lineRequest.getName(), lineRequest.getColor(), null);

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
        List<StationResponse> stationResponses = lineService.getStationsId(lineId)
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
        if (lineService.insertSection( sectionDto, lineId)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @DeleteMapping(value = "/lines/{lineId}/sections")
    public ResponseEntity deleteSection(@PathVariable long lineId, @RequestParam long stationId) {

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

}
