package subway.line;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.line.dto.LineRequest;
import subway.line.dto.LineResponse;
import subway.line.vo.*;
import subway.section.SectionService;
import subway.section.dto.SectionRequest;
import subway.section.vo.SectionCreateValue;
import subway.section.vo.SectionDeleteValue;
import subway.section.vo.SectionReadStationsValue;
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
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest request) {
        LineCreateValue lineCreateValue = new LineCreateValue(request.getName(), request.getColor());
        LineResultValue lineResultValue = lineService.create(lineCreateValue);

        SectionCreateValue sectionCreateValue = new SectionCreateValue(lineResultValue.getID(),
                request.getUpStationID(), request.getDownStationID(), request.getDistance());
        sectionService.create(sectionCreateValue);

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
        LineResultValue lineResultValue = lineService.findByID(new LineReadValue(lineID));
        return ResponseEntity.ok(lineResultValue.toLineResponse(findStationReponses(lineResultValue)));
    }

    @PutMapping("/{lineID}")
    public ResponseEntity<LineResponse> updateLine(@PathVariable Long lineID, @RequestBody LineRequest lineRequest) {
        LineUpdateValue lineUpdateValue = new LineUpdateValue(lineID, lineRequest.getName(), lineRequest.getColor());
        LineResultValue resultValue = lineService.update(lineUpdateValue);
        return ResponseEntity.ok(resultValue.toLineResponse(findStationReponses(resultValue)));
    }

    @DeleteMapping("/{lineID}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long lineID) {
        lineService.delete(new LineDeleteValue(lineID));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{lineID}/sections")
    public ResponseEntity<LineResponse> addSection(@PathVariable Long lineID, @RequestBody SectionRequest request) {
        SectionCreateValue sectionCreateValue = new SectionCreateValue(lineID, request.getUpStationID(),
                request.getDownStationID(), request.getDistance());
        sectionService.create(sectionCreateValue);

        LineResultValue lineResultValue = lineService.findByID(new LineReadValue(lineID));
        return ResponseEntity.ok(lineResultValue.toLineResponse(findStationReponses(lineResultValue)));
    }

    @DeleteMapping("/{lineID}/sections")
    public ResponseEntity<Void> deleteSection(@PathVariable Long lineID, @RequestParam Long stationID) {
        sectionService.delete(new SectionDeleteValue(lineID, stationID));
        return ResponseEntity.ok().build();
    }

    private List<StationResponse> findStationReponses(LineResultValue lineResultValue) {
        SectionReadStationsValue sectionReadStationsValue = new SectionReadStationsValue(lineResultValue.getID());
        StationResultValues stationResultValues = sectionService.findStationsByLineID(sectionReadStationsValue);

        return stationResultValues.allToResponses();
    }
}
