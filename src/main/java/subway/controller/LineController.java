package subway.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.domain.*;
import subway.service.LineService;
import subway.service.SectionService;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class LineController {
    private final LineService lineService;
    private final SectionService sectionService;

    @Autowired
    public LineController(LineService lineService, SectionService sectionService) {
        this.lineService = lineService;
        this.sectionService = sectionService;
    }

    @PostMapping(value = "/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        try {
            lineService.insertLine(new Line(lineRequest));
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
        Line newLine = lineService.findLineByName(lineRequest.getName());
        sectionService.insertFirstSection(new Section(newLine.getId(), newLine.getUpStationId(), newLine.getDownStationId(), lineRequest.getDistance()));
        Stations stations = sectionService.getStationsByLine(newLine);
        return ResponseEntity.created(
                URI.create("/line/" + newLine.getId()))
                .body(new LineResponse(newLine, stations.getStations()));
    }

    @GetMapping("/lines")
    public ResponseEntity<List<LineResponse>> getLines() {
        List<LineResponse> response = lineService.findAll()
                .stream()
                .map(line -> (
                        new LineResponse(line, sectionService.getStationsByLine(line).getStations())
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/lines/{lineId}")
    public ResponseEntity<LineResponse> getLine(@PathVariable Long lineId) {
        Line searchedLine = lineService.findById(lineId);
        Stations stations = sectionService.getStationsByLine(searchedLine);
        return ResponseEntity.ok().body(new LineResponse(searchedLine, stations.getStations()));
    }

    @PutMapping("/lines/{lineId}")
    public ResponseEntity<LineResponse> updateLine(@PathVariable Long lineId, @RequestBody LineRequest lineRequest) {
        lineService.modifyLine(new Line(lineId, lineRequest.getName(), lineRequest.getColor(),
                lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance()));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{lineId}")
    public ResponseEntity<LineResponse> deleteLine(@PathVariable Long lineId) {
        lineService.deleteLine(lineId);
        return ResponseEntity.noContent().build();
    }


}
