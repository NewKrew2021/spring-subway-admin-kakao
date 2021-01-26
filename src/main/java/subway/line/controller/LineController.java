package subway.line.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.line.domain.*;
import subway.line.dto.LineRequest;
import subway.line.dto.LineResponse;
import subway.line.dto.SectionRequest;
import subway.line.service.LineService;
import subway.line.service.SectionService;
import subway.station.dto.StationResponse;
import subway.util.ResponseUtil;

import java.net.URI;
import java.util.List;

@RestController
public class LineController {
    private SectionService sectionService;
    private LineService lineService;

    public LineController(SectionService sectionService, LineService lineService) {
        this.sectionService = sectionService;
        this.lineService = lineService;
    }

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createStation(@RequestBody LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());

        Line newLine = lineService.insert(line, lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance());

        List<StationResponse> stationResponses = ResponseUtil.getStationResponses(lineService.getStations(newLine));

        LineResponse lineResponse = new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor(), stationResponses);

        return ResponseEntity.created(URI.create("/lines/" + newLine.getId())).body(lineResponse);
    }

    @GetMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<LineResponse> responses = ResponseUtil.getLineResponses(lineService.findAll());

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/lines/{lineId}")
    public ResponseEntity<LineResponse> showLine(@PathVariable Long lineId) {
        Line line = lineService.findById(lineId);

        List<StationResponse> stationResponses = ResponseUtil.getStationResponses(lineService.getStations(line));

        LineResponse lineResponse = new LineResponse(line.getId(), line.getName(), line.getColor(), stationResponses);

        return ResponseEntity.ok(lineResponse);
    }

    @PutMapping("/lines/{id}")
    public ResponseEntity<LineResponse> updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        lineService.updateById(id, line);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity deleteStation(@PathVariable Long id) {
        lineService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/lines/{lineId}/sections")
    public ResponseEntity<LineResponse> createSection(@PathVariable Long lineId, @RequestBody SectionRequest sectionRequest) {
        sectionService.insert(SectionFactory.create(lineId, sectionRequest));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{lineId}/sections")
    public ResponseEntity<LineResponse> deleteSection(@PathVariable Long lineId, @RequestParam Long stationId) {
        sectionService.delete(lineId, stationId);
        return ResponseEntity.ok().build();
    }
}