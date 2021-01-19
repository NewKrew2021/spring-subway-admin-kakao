package subway.line;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.section.Section;
import subway.section.SectionRequest;
import subway.section.SectionService;
import subway.station.Station;
import subway.station.StationService;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class LineController {

    private final LineService lineService;
    private final SectionService sectionService;
    private final StationService stationService;

    public LineController(LineService lineService, SectionService sectionService, StationService stationService) {
        this.lineService = lineService;
        this.sectionService = sectionService;
        this.stationService = stationService;
    }

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        if (lineService.existName(lineRequest.getName())) {
            return ResponseEntity.badRequest().build();
        }

        Line newLine = lineService.createLine(Line.fromRequest(lineRequest), lineRequest.getDistance());
        List<Station> stations = stationService.convertIdsToStations(
                sectionService.getStationIdsOfLine(newLine));
        return ResponseEntity.created(URI.create("/lines/" + newLine.getId())).body(newLine.toResponse(stations));
    }

    @GetMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<LineResponse> lineResponses = lineService.getAllLines().stream()
                .map(Line::toResponseWithoutStation)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping(value = "/lines/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLines(@PathVariable long id) {
        Line line = lineService.getLine(id);
        List<Station> stations = stationService.convertIdsToStations(
                sectionService.getStationIdsOfLine(line));
        return ResponseEntity.ok().body(line.toResponse(stations));
    }

    @RequestMapping(value = "/lines/{id}", method = RequestMethod.PUT)
    public ResponseEntity updateLine(@PathVariable long id, @RequestBody LineRequest lineRequest) {
        lineService.updateLine(id, Line.fromRequest(lineRequest));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        lineService.deleteLine(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/lines/{id}/sections")
    public ResponseEntity addSection(@PathVariable Long id, @RequestBody SectionRequest sectionRequest) {
        sectionService.addSection(id, Section.fromRequest(sectionRequest, id));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{id}/sections")
    public ResponseEntity deleteSection(@PathVariable Long id, @RequestParam Long stationId) {
        sectionService.deleteSection(id, stationId);
        return ResponseEntity.ok().build();
    }
}
