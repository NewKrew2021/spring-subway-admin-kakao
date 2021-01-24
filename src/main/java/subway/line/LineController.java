package subway.line;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.line.dto.LineRequest;
import subway.line.dto.LineResponse;
import subway.line.vo.LineAttributes;
import subway.line.vo.LineCreateValue;
import subway.line.vo.LineResultValue;
import subway.section.SectionService;
import subway.section.dto.SectionRequest;
import subway.section.vo.SectionCreateValue;
import subway.station.dto.StationResponse;
import subway.station.vo.StationResultValues;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/lines")
public class LineController {
    private final LineService lineService;
    private final SectionService sectionService;

    public LineController(LineService lineService, SectionService sectionService) {
        this.lineService = lineService;
        this.sectionService = sectionService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        LineResultValue lineResultValue = lineService.create(new LineCreateValue(lineRequest));
        sectionService.create(new SectionCreateValue(lineResultValue.getID(), lineRequest));

        LineResponse lineResponse = lineResultValue.toLineResponse(findStationReponses(lineResultValue));
        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getID())).body(lineResponse);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<LineResponse> lineResultValues = lineService.findAll()
                .stream()
                .map(value -> value.toLineResponse(findStationReponses(value)))
                .collect(Collectors.toList());

        return ResponseEntity.ok(lineResultValues);
    }

    @GetMapping(value = "/{lineID}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLine(@PathVariable Long lineID) {
        LineResultValue lineResultValue = lineService.findByID(lineID);
        return ResponseEntity.ok(lineResultValue.toLineResponse(findStationReponses(lineResultValue)));
    }

    @PutMapping("/{lineID}")
    public ResponseEntity<LineResponse> updateLine(@PathVariable Long lineID, @RequestBody LineRequest lineRequest) {
        LineResultValue resultValue = lineService.update(lineID, new LineAttributes(lineRequest));
        return ResponseEntity.ok(resultValue.toLineResponse(findStationReponses(resultValue)));
    }

    @DeleteMapping("/{lineID}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long lineID) {
        lineService.delete(lineID);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{lineID}/sections")
    public ResponseEntity<LineResponse> addSection(@PathVariable Long lineID,
                                                   @RequestBody SectionRequest sectionRequest) {
        sectionService.create(new SectionCreateValue(lineID, sectionRequest));

        LineResultValue lineResultValue = lineService.findByID(lineID);
        return ResponseEntity.ok(lineResultValue.toLineResponse(findStationReponses(lineResultValue)));
    }

    @DeleteMapping("/{lineID}/sections")
    public ResponseEntity<Void> deleteSection(@PathVariable Long lineID, @RequestParam Long stationID) {
        sectionService.delete(lineID, stationID);
        return ResponseEntity.ok().build();
    }

    private List<StationResponse> findStationReponses(LineResultValue lineResultValue) {
        StationResultValues stationResultValues = sectionService.findStationsByLineID(lineResultValue.getID());
        return stationResultValues.allToResponses();
    }
}
