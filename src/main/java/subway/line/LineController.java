package subway.line;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.exceptions.DuplicateLineNameException;
import subway.exceptions.InvalidLineArgumentException;
import subway.exceptions.InvalidSectionException;
import subway.section.SectionRequest;
import subway.station.Station;
import subway.station.StationResponse;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class LineController {

    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @ExceptionHandler(InvalidSectionException.class)
    public ResponseEntity<String> internalServerErrorHandler(InvalidSectionException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    @ExceptionHandler({InvalidLineArgumentException.class, DuplicateLineNameException.class})
    public ResponseEntity<String> badRequestErrorHandler(RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @PostMapping(value = "/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Line newLine = lineService.save(lineRequest);
        List<Station> stations = lineService.getStationsById(newLine.getId());
        List<StationResponse> stationResponses = stations.stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
        LineResponse lineResponse = new LineResponse(newLine, stationResponses);
        return ResponseEntity.created(URI.create("/lines/" + newLine.getId())).body(lineResponse);
    }

    @GetMapping("/lines/{lineId}")
    public ResponseEntity<LineResponse> showLine(@PathVariable(name = "lineId") long id) {
        Line showLine = lineService.findById(id);
        if (showLine == null) {
            return ResponseEntity.badRequest().build();
        }
        List<Station> stations = lineService.getStationsById(showLine.getId());
        List<StationResponse> stationResponses = stations.stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
        LineResponse lineResponse = new LineResponse(showLine, stationResponses);
        return ResponseEntity.ok().body(lineResponse);
    }

    @GetMapping("/lines")
    public ResponseEntity<List<LineResponse>> showLines() {
        List<Line> lines = lineService.findAll();
        List<LineResponse> lineResponses = lines.stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(lineResponses);
    }

    @PutMapping("/lines/{id}")
    public ResponseEntity updateLine(@PathVariable long id, @RequestBody LineRequest lineRequest) {
        lineService.updateLine(id, lineRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity deleteLine(@PathVariable long id) {
        boolean isLineDeleted = lineService.deleteById(id);
        if (isLineDeleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/lines/{lineId}/sections")
    public ResponseEntity createSection(@PathVariable(name = "lineId") long id, @RequestBody SectionRequest sectionRequest) {
        lineService.saveSection(id, sectionRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{lineId}/sections")
    public ResponseEntity deleteStationInLine(@PathVariable(name = "lineId") long lineId, @RequestParam(name = "stationId") long stationId) {
        lineService.deleteStationById(lineId, stationId);
        return ResponseEntity.ok().build();
    }
}
