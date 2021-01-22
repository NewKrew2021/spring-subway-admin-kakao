package subway.line;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.station.StationDao;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import subway.section.SectionDao;
import subway.station.StationService;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineService lineService;
    private final SectionDao sectionDao;
    private final StationDao stationDao;
    private final StationService stationService;

    public LineController(LineService lineService, SectionDao sectionDao, StationDao stationDao, StationService stationService) {
        this.lineService = lineService;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
        this.stationService = stationService;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Line newline = lineService.saveLine(lineRequest);
        return ResponseEntity
                .created(URI.create(("/lines/" + newline.getId())))
                .body(new LineResponse(newline, stationService.getStationsByLineId(newline.getId())));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        return ResponseEntity.ok().body(lineService.findAll().stream()
                .map((Line line) -> new LineResponse(line, stationService.getStationsByLineId(line.getId())))
                .collect(Collectors.toList()));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        return ResponseEntity.ok().body(new LineResponse(lineService.findById(id), stationService.getStationsByLineId(id)));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        lineService.update(id, new Line(lineRequest.getName(), lineRequest.getColor(), lineRequest.getExtraFare()));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteStation(@PathVariable Long id) {
        lineService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
