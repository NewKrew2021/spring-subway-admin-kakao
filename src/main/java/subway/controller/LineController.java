package subway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Station;
import subway.dto.LineRequest;
import subway.dto.LineResponse;
import subway.dto.StationResponse;
import subway.service.LineService;
import subway.dto.SectionRequest;
import subway.service.SectionService;


import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/lines")
public class LineController {

    private LineService lineService;
    private SectionService sectionService;

    public LineController(LineService lineService, SectionService sectionService){
        this.lineService = lineService;
        this.sectionService = sectionService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Section newSection = new Section(
                new Line(lineRequest),
                new Station(lineRequest.getUpStationId()),
                new Station(lineRequest.getDownStationId()),
                lineRequest.getDistance());

        Line line = lineService.create(newSection);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(
                new LineResponse(line, lineService.getSortedStations(line.getId())));
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> showLines(){
        List<LineResponse> lineResponses = lineService.showLines()
                .stream()
                .map(line -> new LineResponse(line, lineService.getSortedStations(line.getId())))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        LineResponse lineResponse = new LineResponse(lineService.showLine(id), lineService.getSortedStations(id));
        return ResponseEntity.ok().body(lineResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity modifyLine(@PathVariable Long id, @RequestBody LineRequest lineRequest){
        lineService.modify(id, new Line(lineRequest));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        lineService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/sections")
    public ResponseEntity addSection(@PathVariable Long id, @RequestBody SectionRequest sectionRequest) {
        Section section = new Section(
                new Station(sectionRequest.getUpStationId()),
                new Station(sectionRequest.getDownStationId()),
                sectionRequest.getDistance());
        sectionService.add(id, section);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/sections")
    public ResponseEntity deleteSection(@PathVariable Long id, @RequestParam Long stationId) {
        sectionService.delete(id, stationId);
        return ResponseEntity.ok().build();
    }
}
