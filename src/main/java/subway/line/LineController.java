package subway.line;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.exceptions.DuplicateLineNameException;
import subway.exceptions.InvalidLineArgumentException;
import subway.exceptions.InvalidSectionException;
import subway.station.StationResponse;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("lines")
public class LineController {
    @Autowired
    private LineService lineService;

    @ExceptionHandler(InvalidSectionException.class)
    public ResponseEntity<String> internalServerErrorHandler(InvalidSectionException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    @ExceptionHandler({InvalidLineArgumentException.class, DuplicateLineNameException.class})
    public ResponseEntity<String> badRequestErrorHandler(RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Line newLine = lineService.save(lineRequest);
        List<StationResponse> stationResponses = lineService.getStationResponsesById(newLine.getId());
        LineResponse lineResponse = new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor(), stationResponses);
        return ResponseEntity.created(URI.create("/lines/" + newLine.getId())).body(lineResponse);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> showLines() {
        List<Line> lines = lineService.findAll();
        List<LineResponse> lineResponses = new ArrayList<>();
        for (Line line : lines) {
            lineResponses.add(new LineResponse(line.getId(), line.getName(), line.getColor()));
        }
        return ResponseEntity.ok().body(lineResponses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LineResponse> updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        lineService.updateLine(id, lineRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<LineResponse> deleteLine(@PathVariable Long id) {
        boolean isLineDeleted = lineService.deleteById(id);
        if (isLineDeleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/{lineId}")
    public ResponseEntity<LineResponse> showLine(@PathVariable(name = "lineId") Long id) {
        Line showLine = lineService.findById(id);
        if (showLine == null) {
            return ResponseEntity.badRequest().build();
        }
        List<StationResponse> stationResponses = lineService.getStationResponsesById(showLine.getId());
        LineResponse lineResponse = new LineResponse(showLine.getId(), showLine.getName(), showLine.getColor(), stationResponses);
        return ResponseEntity.ok().body(lineResponse);
    }

    @PostMapping("/{lineId}/sections")
    public ResponseEntity createSection(@PathVariable(name = "lineId") Long id, @RequestBody SectionRequest sectionRequest) {
        lineService.saveSection(id, sectionRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{lineId}/sections")
    public ResponseEntity deleteStationInLine(@PathVariable(name = "lineId") Long lineId, @RequestParam(name = "stationId") Long stationId) {
        lineService.deleteStationById(lineId, stationId);
        return ResponseEntity.ok().build();
    }
}
