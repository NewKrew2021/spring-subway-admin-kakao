package subway.line;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.exception.ElementDeleteExeption;
import subway.exception.ElementInsertException;
import subway.section.SectionRequest;
import subway.section.SectionService;
import subway.station.StationResponse;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class LineController {

    private final LineService lineService;
    private final SectionService sectionService;

    public LineController(LineService lineService, SectionService sectionService) {
        this.lineService = lineService;
        this.sectionService = sectionService;
    }

    @PostMapping(value = "/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        if (lineService.isLineNameExist(lineRequest.getName())) {
            return ResponseEntity.badRequest().build();
        }

        Line newLine = lineService.save(lineRequest);
        sectionService.initialSave(newLine, lineRequest);

        return ResponseEntity.created(URI.create("/lines/" + newLine.getId()))
                .body(new LineResponse(newLine, Collections.emptyList()));
    }

    @GetMapping("/lines/{id}")
    public ResponseEntity<LineResponse> getLineStations(@PathVariable Long id) {
        Line line = lineService.findById(id);
        return ResponseEntity.ok().body(new LineResponse(line, getStationsResponseOfLineId(id)));
    }

    @PutMapping("/lines/{id}")
    public ResponseEntity modifyLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        lineService.modify(id, lineRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity deleteLineById(@PathVariable Long id) {
        lineService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/lines/{id}/sections")
    public ResponseEntity addSection(@PathVariable Long id, @RequestBody SectionRequest sectionRequest) {
        if (sectionService.hasSectionOverlapped(id, sectionRequest)) {
            throw new ElementInsertException("노선에 겹치는 구간이 존재합니다.");
        }
        sectionService.addSection(id, sectionRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{id}/sections")
    public ResponseEntity deleteSectionByStationId(@PathVariable Long id, @RequestParam Long stationId) {
        if (lineService.isNotDeletable(id)) {
            throw new ElementDeleteExeption("최소 구간은 삭제할 수 없습니다.");
        }
        lineService.deleteStation(id, stationId);
        return ResponseEntity.ok().build();
    }

    private List<StationResponse> getStationsResponseOfLineId(Long lineId) {
        return lineService.findStationsOfLine(lineId).stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/lines")
    public ResponseEntity<List<LineResponse>> getAllLines() {
        return ResponseEntity.ok().body(lineService.findAll().stream()
                .map(line -> new LineResponse(line, getStationsResponseOfLineId(line.getId())))
                .collect(Collectors.toList()));
    }

}
