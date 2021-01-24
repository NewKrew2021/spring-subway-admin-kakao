package subway.line;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import subway.line.dto.LineRequest;
import subway.line.dto.LineResponse;
import subway.line.vo.LineAttributes;
import subway.line.vo.LineCreateValue;
import subway.line.vo.LineResultValue;
import subway.section.SectionService;
import subway.section.dto.SectionRequest;
import subway.section.vo.SectionCreateValue;

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

    @Transactional
    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        long newLineID = lineService.create(new LineCreateValue(lineRequest));
        sectionService.create(new SectionCreateValue(newLineID, lineRequest));

        LineResponse lineResponse = LineResponse.of(lineService.findByID(newLineID));
        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getID())).body(lineResponse);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<LineResponse> lineResultValues = lineService.findAll()
                .stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());

        return ResponseEntity.ok(lineResultValues);
    }

    @GetMapping(value = "/{lineID}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLine(@PathVariable Long lineID) {
        LineResultValue lineResultValue = lineService.findByID(lineID);
        return ResponseEntity.ok(LineResponse.of(lineResultValue));
    }

    @PutMapping("/{lineID}")
    public ResponseEntity<LineResponse> updateLine(@PathVariable Long lineID, @RequestBody LineRequest lineRequest) {
        LineResultValue resultValue = lineService.update(lineID, new LineAttributes(lineRequest));
        return ResponseEntity.ok(LineResponse.of(resultValue));
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
        return ResponseEntity.ok(LineResponse.of(lineResultValue));
    }

    @DeleteMapping("/{lineID}/sections")
    public ResponseEntity<Void> deleteSection(@PathVariable Long lineID, @RequestParam Long stationID) {
        sectionService.delete(lineID, stationID);
        return ResponseEntity.ok().build();
    }
}
