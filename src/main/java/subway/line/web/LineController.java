package subway.line.web;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.line.dto.LineRequest;
import subway.line.dto.LineResponse;
import subway.line.service.LineService;
import subway.line.vo.Line;
import subway.section.dto.SectionRequest;
import subway.section.service.SectionService;
import subway.section.vo.Section;
import subway.station.service.StationService;
import subway.station.vo.Stations;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

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
                lineRequest.toLine()
        );
        Section section = sectionService.create(
                lineRequest.toSection(
                        line.getId()
                )
        );
        Stations stations = stationService.findStationsByIds(section.getStationIds());
        return ResponseEntity.created(
                URI.create("/lines/" + line.getId())
        ).body(new LineResponse(line, stations));
    }

    @GetMapping(value = "/lines/{lineId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLine(@PathVariable Long lineId) {
        Line line = lineService.findLineById(lineId);
        Stations stations = stationService.findStationsByIds(
                sectionService.findSectionsByLineId(lineId)
                        .getStationIdsInDownwardOrder()
        );
        return ResponseEntity.ok(
                new LineResponse(line, stations)
        );
    }

    @GetMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        return lineService.findAllLines()
                .stream()
                .map(line -> {
                    Stations stations = stationService.findStationsByIds(
                            sectionService.findSectionsByLineId(line.getId())
                                    .getStationIdsInDownwardOrder()
                    );
                    return new LineResponse(line, stations);
                }).collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        ResponseEntity::ok
                ));
    }

    @PutMapping(value = "/lines/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateLine(@RequestBody LineRequest lineRequest, @PathVariable Long id) {
        lineService.update(
                lineRequest.toLine(id)
        );
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
        Section section = sectionRequest.toSection(lineId);
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
