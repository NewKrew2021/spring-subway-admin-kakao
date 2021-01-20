package subway.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.domain.line.Line;
import subway.domain.line.LineRequest;
import subway.domain.line.LineResponse;
import subway.service.LineService;
import subway.domain.section.Section;
import subway.domain.section.SectionRequest;
import subway.service.SectionService;
import subway.domain.station.Station;
import subway.domain.station.StationResponse;
import subway.service.StationService;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineService lineService;
    private final SectionService sectionService;
    private final StationService stationService;

    public LineController(LineService lineService, SectionService sectionService, StationService stationService) {
        this.lineService = lineService;
        this.sectionService = sectionService;
        this.stationService = stationService;
    }

    @PostMapping("")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Line newLine = lineService.createLine(lineRequest.getName(), lineRequest.getColor());
        List<StationResponse> stationResponses = new ArrayList<>();
        sectionService.createSection(lineRequest.getUpStationId(), 0, newLine.getId());
        sectionService.createSection(lineRequest.getDownStationId(), lineRequest.getDistance(), newLine.getId());

        Station upStation = stationService.getStation(lineRequest.getUpStationId());
        Station downStation = stationService.getStation(lineRequest.getDownStationId());
        stationResponses.add(new StationResponse(upStation.getId(), upStation.getName()));
        stationResponses.add(new StationResponse(downStation.getId(), downStation.getName()));

        return ResponseEntity.created(URI.create("/lines/" + newLine.getId())).body(new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor(), stationResponses));
    }

    @GetMapping(value = "")
    public ResponseEntity<List<LineResponse>> showLines() {
        List<LineResponse> lineResponses = lineService.getAllLines();
        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<LineResponse> showLines(@PathVariable long id) {
        Line line = lineService.getLine(id);
        List<Station> stations = sectionService.getStationsOfLine(id);
        List<StationResponse> stationResponses = stations.stream()
                .map(station -> new StationResponse(station.getId(), station.getName()))
                .collect(Collectors.toList());
        LineResponse lineResponse = new LineResponse(line.getId(), line.getName(), line.getColor(), stationResponses);
        return ResponseEntity.ok().body(lineResponse);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity updateLine(@PathVariable long id, @RequestBody LineRequest lineRequest) {
        lineService.updateLine(id, lineRequest.getName(), lineRequest.getColor());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        lineService.deleteLine(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{id}/sections")
    public ResponseEntity addSection(@PathVariable Long id, @RequestBody SectionRequest sectionRequest) {
        sectionService.addSection(id, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/sections")
    public ResponseEntity deleteSection(@PathVariable Long id, @RequestParam Long stationId) {
        sectionService.deleteSection(id, stationId);
        return ResponseEntity.ok().build();
    }
}
