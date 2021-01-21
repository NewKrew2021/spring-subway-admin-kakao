package subway.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Station;
import subway.dto.*;
import subway.exception.DuplicateLineNameException;
import subway.exception.LineNotFoundException;
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
        try{
            System.out.println(lineRequest);
            lineService.insertLine(lineRequest.toLine());
            Line newLine = lineService.findLineByName(lineRequest.getName());
            sectionService.insertFirstSection(new Section(newLine.getId(), newLine.getUpStationId(), newLine.getDownStationId(), lineRequest.getDistance()));
            List<Station> stations = sectionService.getStationsByLine(newLine);
            return ResponseEntity.created(
                    URI.create("/line/" + newLine.getId()))
                    .body(new LineResponse(newLine, stations));
        }
        catch (DuplicateLineNameException e){
            return ResponseEntity.badRequest().build();
        }
        catch (LineNotFoundException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/lines")
    public ResponseEntity<List<LineResponse>> getLines() {
        List<LineResponse> response = lineService.findAll()
                .stream()
                .map(line -> (
                        new LineResponse(line, sectionService.getStationsByLine(line))
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/lines/{lineId}")
    public ResponseEntity<LineResponse> getLine(@PathVariable Long lineId) {
        Line searchedLine = lineService.findById(lineId);
        List<Station> stations = sectionService.getStationsByLine(searchedLine);
        return ResponseEntity.ok().body(new LineResponse(searchedLine, stations));
    }

    @PutMapping("/lines/{lineId}")
    public ResponseEntity<LineResponse> updateLine(@PathVariable Long lineId, @RequestBody LineRequest lineRequest) {
        lineService.modifyLine(lineRequest.toLine(lineId));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{lineId}")
    public ResponseEntity<LineResponse> deleteLine(@PathVariable Long lineId) {
        lineService.deleteLine(lineId);
        return ResponseEntity.noContent().build();
    }


}
