package subway.line.web;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.line.dto.LineRequest;
import subway.line.dto.LineResponse;
import subway.line.entity.Line;
import subway.line.service.LineService;
import subway.section.dto.SectionRequest;
import subway.section.entity.Section;
import subway.section.service.SectionService;
import subway.station.entity.Stations;
import subway.station.service.StationService;

import java.net.URI;
import java.util.List;

@RestController
public class LineController {
    private final LineService lineService;
    private final StationService stationService;
    private final SectionService sectionService;

    public LineController(LineService lineService, StationService stationService, SectionService sectionService) {
        this.lineService = lineService;
        this.stationService = stationService;
        this.sectionService = sectionService;
    }

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Line line = lineService.create(
                lineRequest.getName(),
                lineRequest.getColor()
        );
        Section section = sectionService.create(line.getId(),
                lineRequest.getUpStationId(),
                lineRequest.getDownStationId(),
                lineRequest.getDistance());
        Stations stations = stationService.getStationsByIds(section.getStationIds());
        return ResponseEntity.created(
                URI.create("/lines/" + line.getId())
        ).body(new LineResponse(line, stations));
    }

    @GetMapping(value = "/lines/{lineId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLine(@PathVariable Long lineId) {
        return ResponseEntity.ok(lineService.getLineWithStationsByLineId(lineId));
    }

    @GetMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        return ResponseEntity.ok(lineService.getAllLinesWithStations());
    }

    @PutMapping(value = "/lines/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateLine(@RequestBody LineRequest lineRequest, @PathVariable Long id) {
        Line line = new Line(id,
                lineRequest.getName(),
                lineRequest.getColor());
        lineService.update(line);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        sectionService.deleteByLineId(id);
        lineService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/lines/{lineId}/sections")
    public ResponseEntity<Void> createSection(@PathVariable Long lineId, @RequestBody SectionRequest sectionRequest) {
        Section section = new Section(lineId,
                sectionRequest.getUpStationId(),
                sectionRequest.getDownStationId(),
                sectionRequest.getDistance());
        sectionService.connect(section);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{lineId}/sections")
    public ResponseEntity<Void> deleteSections(@PathVariable Long lineId, @RequestParam Long stationId) {
        sectionService.deleteByLineIdAndStationId(lineId, stationId);
        stationService.delete(stationId);
        return ResponseEntity.ok().build();
    }
}
