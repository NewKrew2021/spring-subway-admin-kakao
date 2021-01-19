package subway.line;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.exception.InvalidIdException;
import subway.section.*;
import subway.station.Station;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class LineController {
    private final LineService lineService;

    @Autowired
    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity illegalArgumentExceptionHandler() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @ExceptionHandler(value = {DataAccessException.class, InvalidIdException.class})
    public ResponseEntity exceptionHandler() {
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Line line = lineRequest.toLine();
        Section section = new Section(new Station(lineRequest.getUpStationId()), new Station(lineRequest.getDownStationId()), lineRequest.getDistance());
        Line newLine = lineService.createLine(line, section);
        LineResponse lineResponse = new LineResponse(newLine);
        return ResponseEntity.created(URI.create("/lines/" + newLine.getId())).body(lineResponse);
    }

    @GetMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<LineResponse> response = lineService.showLines().stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/lines/{id}")
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        Line line = lineService.showLine(id);
        return ResponseEntity.ok().body(new LineResponse(line));
    }

    @PutMapping("/lines/{id}")
    public ResponseEntity modifyLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        lineService.modifyLine(id, lineRequest.toLine());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        lineService.deleteLine(id);
        return ResponseEntity.noContent().build();
    }

}
