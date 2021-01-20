package subway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.convertor.LineConvertor;
import subway.domain.Section;
import subway.dto.LineRequest;
import subway.dto.LineResponse;
import subway.dto.LineResponseWithStation;
import subway.factory.LineFactory;
import subway.service.LineService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Section section = new Section(lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance());
        LineResponse lineResponse = LineConvertor.convertLine(lineService.save(LineFactory.getLine(lineRequest),section));
        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId())).body(lineResponse);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> showLines() {
        return ResponseEntity.ok(LineConvertor.convertLines(lineService.findAll()));
    }

    //TODO 리스폰스 변경.
    @GetMapping("/{lineId}")
    public ResponseEntity<LineResponseWithStation> showLine(@PathVariable Long lineId) {
        LineResponseWithStation lineResponseWithStation = lineService.findOneResponse(lineId);
        return ResponseEntity.ok(lineResponseWithStation);
    }

    @PutMapping("/{lineId}")
    public ResponseEntity updateLine(@PathVariable Long lineId, @RequestBody LineRequest lineRequest) {
        if (!lineService.update(LineFactory.getLine(lineId, lineRequest))) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{lineId}")
    public ResponseEntity deleteLine(@PathVariable Long lineId) {
        if (!lineService.deleteById(lineId))
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok().build();
    }
}
