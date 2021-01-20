package subway.line;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import subway.section.Section;
import subway.section.SectionDto;
import subway.section.SectionRequest;
import subway.section.SectionService;
import subway.station.Station;
import subway.station.StationResponse;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class LineController {
    LineService lineService;
    SectionService sectionService;

    public LineController(LineService lineService, SectionService sectionService) {
        this.lineService = lineService;
        this.sectionService = sectionService;
    }

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        LineDto lineDto = new LineDto(lineRequest);
        SectionDto sectionDto = new SectionDto(lineRequest);

        Line newLine = lineService.createLine(lineDto, sectionDto);
        LineResponse lineResponse = new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor());
        return ResponseEntity.created(URI.create("/lines/" + newLine.getId())).body(lineResponse);
    }

    @PostMapping("/lines/{lineId}/sections")
    public ResponseEntity createSection(@RequestBody SectionRequest sectionRequest,
                                        @PathVariable Long lineId) {

        SectionDto sectionDto = new SectionDto(sectionRequest);
        sectionService.createSection(lineId, sectionDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<LineResponse> lineResponses =
                lineService.findAllLines().stream().map(LineResponse::new).collect(Collectors.toList());

        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping(value = "/lines/{id}")
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        Line targetLine = lineService.findLineById(id);
        List<Station> stations = sectionService.findSortedStationsByLineId(id);

        List<StationResponse> stationResponses =
                stations.stream().map(StationResponse::new).collect(Collectors.toList());

        LineResponse lineResponse = new LineResponse(targetLine, stationResponses);
        return ResponseEntity.ok().body(lineResponse);
    }

    @PutMapping(value = "/lines/{id}")
    public ResponseEntity modifyLine(@RequestBody LineRequest lineRequest,
                                     @PathVariable Long id) {
        LineDto lineDto = new LineDto(lineRequest);
        lineService.updateLine(id, lineDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/lines/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        lineService.deleteLineById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/lines/{lineId}/sections")
    public ResponseEntity deleteSection(@PathVariable Long lineId,
                                        @RequestParam Long stationId) {

        sectionService.deleteStation(lineId, stationId);
        return ResponseEntity.ok().build();
    }
}
