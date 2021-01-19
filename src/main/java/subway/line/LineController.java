package subway.line;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.station.StationService;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class LineController {

    @Resource
    private StationService stationService;
    @Resource
    private LineService lineService;
    @Resource
    private SectionService sectionService;

    @PostMapping(value = "/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        return lineService.create(lineRequest);
    }

    @GetMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        return lineService.getLines();
    }

    @DeleteMapping("/lines/{lineId}")
    public ResponseEntity deleteLine(@PathVariable Long lineId) {
        return lineService.delete(lineId);
    }

    @GetMapping(value = "/lines/{lineId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLine(@PathVariable Long lineId) {
        return lineService.getLine(lineId);
    }

    @PutMapping(value = "/lines/{lineId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateLine(@PathVariable Long lineId, @RequestBody LineRequest lineRequest) {
        return lineService.update(lineId, lineRequest);
    }

    @PostMapping(value = "/lines/{lineId}/sections")
    public ResponseEntity createSection(@PathVariable Long lineId, @RequestBody SectionRequest sectionRequest) {
        sectionService.validate(lineId, sectionRequest, stationService.getStations(lineId));
        return sectionService.create(lineId, sectionRequest);
    }

    @DeleteMapping(value = "/lines/{lineId}/sections")
    public ResponseEntity deleteSection(@PathVariable Long lindId, @RequestParam Long stationId) {
        return sectionService.delete(lindId, stationId);
    }
}
