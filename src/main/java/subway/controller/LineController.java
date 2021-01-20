package subway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.domain.Line;
import subway.domain.Section;
import subway.response.*;
import subway.request.LineRequest;
import subway.request.SectionRequest;
import subway.service.LineService;
import subway.service.SectionService;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class LineController {

    private final LineService lineService;
    private final SectionService sectionService;

    public LineController(LineService lineService,
                          SectionService sectionService) {
        this.lineService = lineService;
        this.sectionService = sectionService;
    }

    @PostMapping(value = "/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Line line = lineService.getLine(
                lineRequest.getName(),
                lineRequest.getColor());
        lineService.creatSection(
                lineRequest.getUpStationId(),
                lineRequest.getDownStationId(),
                lineRequest.getDistance(),
                line);
        LineResponse lineResponse = new LineResponse(line);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(lineResponse);
    }

    @GetMapping("/lines")
    public ResponseEntity<List<LineResponse>> showAllLines() {
        List<LineResponse> responses = lineService.findAllLines().stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(responses);
    }

    @GetMapping("/lines/{id}")
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        Line line = lineService.findOneLine(id);
        LineResponse response = new LineResponse(line);
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/lines/{id}")
    public ResponseEntity<LineResponse> updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        Line line = lineService.findOneLine(id);
        LineResponse response = new LineResponse(lineService.updateLine(id, line));
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity<LineResponse> deleteLine(@PathVariable Long id) {
        lineService.deleteLineById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/lines/{lineId}/sections")
    public ResponseEntity<LineResponse> deleteStationOnLine(@PathVariable Long lineId, @RequestParam("stationId") Long stationId) {
        Section previous = sectionService.getOneByLineIdAndStationId(lineId, stationId, false);
        Section next = sectionService.getOneByLineIdAndStationId(lineId, stationId, true);
        lineService.deleteStationOnLine(stationId, previous, next);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/lines/{lineId}/sections")
    public ResponseEntity<SectionResponse> createSectionOnLine(@PathVariable Long lineId, @RequestBody SectionRequest sectionRequest) {
        Line line = lineService.findOneLine(lineId);
        Long upStationId = sectionRequest.getUpStationId();
        Long downStationId = sectionRequest.getDownStationId();
        Section section = sectionService.getSection(
                line,
                upStationId,
                downStationId,
                sectionRequest.getDistance());

        lineService.checkDuplicateName(lineId, upStationId, downStationId);

        lineService.addSection(line, section);
        SectionResponse response = new SectionResponse(section);
        return ResponseEntity.ok().body(response);
    }
}
