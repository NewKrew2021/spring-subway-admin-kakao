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

    @PostMapping(value = "/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        if (lineRequest.getDownStationId() == null || lineRequest.getUpStationId() == null || lineRequest.getDistance() == 0) {
            throw new InvalidLineArgumentException("모든 정보를 입력해주세요.");
        }
        if (lineRequest.getDownStationId() == lineRequest.getUpStationId()) {
            throw new InvalidLineArgumentException("상행종점과 하행종점은 같을 수 없습니다.");
        }
        Line newLine = lineService.save(lineRequest);

        List<StationResponse> stationResponses = lineService.getStationResponsesById(newLine.getId());
        LineResponse lineResponse = new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor(), stationResponses);

        return ResponseEntity.created(URI.create("/lines/" + newLine.getId())).body(lineResponse);
    }

    @GetMapping("/lines/{lineId}")
    public ResponseEntity<LineResponse> showLine(@PathVariable(name = "lineId") Long id) {
        Line showLine = lineService.findById(id);
        if (showLine == null) {
            return ResponseEntity.badRequest().build();
        }
        List<StationResponse> stationResponses = lineService.getStationResponsesById(showLine.getId());
        LineResponse lineResponse = new LineResponse(showLine.getId(), showLine.getName(), showLine.getColor(), stationResponses);

        return ResponseEntity.ok().body(lineResponse);
    }

    @GetMapping("/lines")
    public ResponseEntity<List<LineResponse>> showLines() {
        List<Line> lines = lineService.findAll();
        List<LineResponse> lineResponses = new ArrayList<>();
        for (Line line : lines) {
            lineResponses.add(new LineResponse(line.getId(), line.getName(), line.getColor()));
        }
        return ResponseEntity.ok().body(lineResponses);
    }

    @PutMapping("/lines/{id}")
    public ResponseEntity<LineResponse> updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        lineService.updateLine(id, lineRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity<LineResponse> deleteLine(@PathVariable Long id) {
        boolean isLineDeleted = lineService.deleteById(id);
        if (isLineDeleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/lines/{lineId}/sections")
    public ResponseEntity createSection(@PathVariable(name = "lineId") Long id, @RequestBody SectionRequest sectionRequest) {
        Section section = new Section(sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());
        Line line = lineService.saveSection(id, sectionRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path = "/lines/{lineId}/sections")
    public ResponseEntity deleteStationInLine(@PathVariable(name = "lineId") Long lineId, @RequestParam(name = "stationId") Long stationId) {
        lineService.deleteStationById(lineId, stationId);
        return ResponseEntity.ok().build();
    }
}
