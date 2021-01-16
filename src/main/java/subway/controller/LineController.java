package subway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import subway.domain.Station;
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
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        try {
            LineResponse lineResponse = lineService.createLine(lineRequest);
            return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId())).body(lineResponse);
        } catch (DataAccessException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> getLines() {
        List<LineResponse> responses = lineService.getLines();
        return ResponseEntity.ok().body(responses);
    }

    @GetMapping(value = "/{lineId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> getLine(@PathVariable Long lineId) {
        try {
            LineResponse lineResponse = lineService.getLine(lineId);
            return ResponseEntity.ok().body(lineResponse);
        } catch (DataAccessException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity modifyLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        try {
            boolean response = lineService.modifyLine(id, lineRequest);
            return response ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
        } catch (DataAccessException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        boolean response = lineService.deleteLine(id);
        return response ? ResponseEntity.noContent().build() : ResponseEntity.badRequest().build();
    }

    // TODO 공통적 ExceptionHandler 작성
    @Transactional
    @PostMapping(value = "/{id}/sections", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SectionResponse> addSectionToLine(@RequestBody SectionRequest sectionRequest,
                                                            @PathVariable Long id) {
        try {
            validateLineId(id, sectionRequest.getLineId());
            SectionResponse sectionResponse = sectionService.addSectionToLine(sectionRequest);
            return ResponseEntity.created(URI.create("/lines/" + sectionRequest.getLineId() +
                    "/sections/" + sectionResponse.getId())).body(sectionResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Transactional
    @DeleteMapping("/{lineId}/sections")
    public ResponseEntity deleteStationFromLine(@PathVariable Long lineId, @RequestParam Long stationId) {
        try {
            List<Station> stations = lineService.getOrderedStationsOfLine(lineId);
            validateDeletable(stationId, stations);
            sectionService.deleteStationFromLine(lineId, stationId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private void validateLineId(Long id, Long lineId) {
        if (!id.equals(lineId)) {
            throw new IllegalArgumentException();
        }
    }

    private void validateDeletable(Long stationId, List<Station> stations) {
        if (stations.stream().noneMatch(station -> station.getId().equals(stationId)) || stations.size() <= 2) {
            throw new IllegalArgumentException();
        }
    }
}
