package subway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import subway.domain.OrderedStations;
import subway.exception.custom.CannotDeleteSectionException;
import subway.exception.custom.DifferentLineIdException;
import subway.request.LineRequest;
import subway.request.SectionRequest;
import subway.response.LineResponse;
import subway.response.SectionResponse;
import subway.service.LineService;
import subway.service.SectionService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/lines")
public class LineController {
    private final LineService lineService;
    private final SectionService sectionService;

    @Autowired
    public LineController(LineService lineService, SectionService sectionService) {
        this.lineService = lineService;
        this.sectionService = sectionService;
    }

    @Transactional
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        LineResponse lineResponse = lineService.createLine(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId())).body(lineResponse);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> getLines() {
        return ResponseEntity.ok().body(lineService.getLines());
    }

    @GetMapping(value = "/{lineId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> getLine(@PathVariable Long lineId) {
        return ResponseEntity.ok().body(lineService.getLine(lineId));
    }

    @PutMapping(value = "/{lineId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity modifyLine(@PathVariable Long lineId, @RequestBody LineRequest lineRequest) {
        return lineService.modifyLine(lineId, lineRequest) ?
                ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @Transactional
    @DeleteMapping("/{lineId}")
    public ResponseEntity deleteLine(@PathVariable Long lineId) {
        return lineService.deleteLine(lineId) ?
                ResponseEntity.noContent().build() : ResponseEntity.badRequest().build();
    }

    @Transactional
    @PostMapping(value = "/{lineId}/sections", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SectionResponse> addSectionToLine(@RequestBody SectionRequest sectionRequest,
                                                            @PathVariable Long lineId) {
        validateLineId(lineId, sectionRequest.getLineId());
        SectionResponse sectionResponse = sectionService.addSectionToLine(sectionRequest);
        return ResponseEntity.created(URI.create("/lines/" + sectionRequest.getLineId() +
                "/sections/" + sectionResponse.getId())).body(sectionResponse);
    }

    @Transactional
    @DeleteMapping("/{lineId}/sections")
    public ResponseEntity deleteStationFromLine(@PathVariable Long lineId, @RequestParam Long stationId) {
        validateDeletable(stationId, lineService.getOrderedStationsOfLine(lineId));
        sectionService.deleteStationFromLine(lineId, stationId);
        return ResponseEntity.noContent().build();
    }

    private static void validateLineId(Long id, Long lineId) {
        if (!id.equals(lineId)) {
            throw new DifferentLineIdException();
        }
    }

    private static void validateDeletable(Long stationId, OrderedStations orderedStations) {
        if (!orderedStations.hasStation(stationId) || orderedStations.size() <= 2) {
            throw new CannotDeleteSectionException();
        }
    }
}
