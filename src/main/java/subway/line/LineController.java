package subway.line;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.section.Section;
import subway.section.SectionService;
import subway.section.Sections;
import subway.station.Station;
import subway.station.StationResponse;
import subway.station.StationService;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineService lineService;
    private final StationService stationService;
    private final SectionService sectionService;

    public LineController(LineService lineService, StationService stationService, SectionService sectionService) {
        this.lineService = lineService;
        this.stationService = stationService;
        this.sectionService = sectionService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Line newLine = lineService.save(
                new Line(lineRequest.getName(),
                        lineRequest.getColor(),
                        lineRequest.getUpStationId(),
                        lineRequest.getDownStationId()),

                new Section(lineRequest.getUpStationId(),
                        lineRequest.getDownStationId(),
                        lineRequest.getDistance()));
        if (newLine == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.created(URI.create("/lines/" + newLine.getId()))
                .body(new LineResponse(newLine.getId(),
                        newLine.getName(),
                        newLine.getColor()));
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> showLines() {
        return ResponseEntity.ok(lineService.findAll()
                .stream()
                .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor()))
                .collect(Collectors.toList()));
    }

    @GetMapping("/{lineId}")
    public ResponseEntity<LineResponse> showLine(@PathVariable Long lineId) {
        Line newLine = lineService.findOne(lineId);

        Sections sections = sectionService.getSectionsByLineId(lineId);

        Set<Long> stationIds = new LinkedHashSet<>();

        for (Section section : sections.getSections()) {
            stationIds.add(section.getUpStationId());
            stationIds.add(section.getDownStationId());
        }

        List<StationResponse> stationResponses = stationIds.stream()
                .map(id -> {
                    Station station = stationService.findOne(id);
                    return new StationResponse(station.getId(), station.getName());
                }).collect(Collectors.toList());

        return ResponseEntity.ok(new LineResponse(
                newLine.getId(),
                newLine.getName(),
                newLine.getColor(),
                stationResponses));
    }

    @PutMapping("/{lineId}")
    public ResponseEntity updateLine(@PathVariable Long lineId, @RequestBody LineRequest lineRequest) {
        if (!lineService.update(new Line(lineId, lineRequest.getName(), lineRequest.getColor()))) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{lineId}")
    public ResponseEntity deleteLine(@PathVariable Long lineId) {
        if (!lineService.deleteById(lineId))
            return ResponseEntity.badRequest().build();
        return ResponseEntity.noContent().build();
    }
}
