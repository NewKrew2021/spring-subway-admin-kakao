package subway.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.domain.Line;
import subway.request.LineRequest;
import subway.response.LineResponse;
import subway.domain.Section;
import subway.request.SectionRequest;
import subway.service.LineService;
import subway.service.SectionService;
import subway.domain.Station;
import subway.response.StationResponse;
import subway.service.StationService;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/lines")
@RestController
public class LineController {
    SectionService sectionService;
    LineService lineService;
    StationService stationService;

    public LineController(SectionService sectionService, LineService lineService, StationService stationService) {
        this.sectionService = sectionService;
        this.lineService = lineService;
        this.stationService = stationService;
    }

    @PostMapping("")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Line line = lineRequest.toLine();
        Line newLine = lineService.createLine(line);

        Station upStation = stationService.getStation(lineRequest.getUpStationId());
        Station downStation = stationService.getStation(lineRequest.getDownStationId());

        Section section = new Section(newLine, upStation, downStation, lineRequest.getDistance());
        sectionService.save(section);

        LineResponse lineResponse = new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor());

        return ResponseEntity.created(URI.create("/lines/" + newLine.getId())).body(lineResponse);
    }

    @PostMapping("/{lineId}/sections")
    public ResponseEntity createSection(@RequestBody SectionRequest sectionRequest,
                                        @PathVariable Long lineId) {
        Line line = lineService.getLine(lineId);
        Station upStation = stationService.getStation(sectionRequest.getUpStationId());
        Station downStation = stationService.getStation(sectionRequest.getDownStationId());

        Section section = new Section(line, upStation, downStation, sectionRequest.getDistance());
        sectionService.save(section);

        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        return ResponseEntity.ok().body(lineService.getLines().stream().map(LineResponse::from).collect(Collectors.toList()));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        Line line = lineService.getLine(id);

        List<StationResponse> stationResponses = sectionService.findSortedStationsByLine(line)
                .stream()
                .map(Station::mapToResponse)
                .collect(Collectors.toList());

        LineResponse lineResponse = LineResponse.from(line, stationResponses);

        return ResponseEntity.ok().body(lineResponse);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity modifyLine(@RequestBody LineRequest lineRequest,
                                     @PathVariable Long id) {
        lineService.updateLine(new Line(id, lineRequest.getName(), lineRequest.getColor()));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        lineService.deleteLine(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/{lineId}/sections")
    public ResponseEntity deleteSection(@PathVariable Long lineId,
                                        @RequestParam Long stationId) {
        Station station = stationService.getStation(stationId);
        Line line = lineService.getLine(lineId);
        sectionService.deleteStation(line, station);

        return ResponseEntity.ok().build();
    }
}
