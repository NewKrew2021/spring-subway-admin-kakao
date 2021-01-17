package subway.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.dto.Line;
import subway.dao.LineDao;
import subway.dto.LineRequest;
import subway.dto.LineResponse;
import subway.dto.Section;
import subway.dao.SectionDao;
import subway.service.LineService;
import subway.service.SectionService;
import subway.dto.Station;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class LineController {
    private final LineService lineService;
    private final SectionService sectionService;

    @Autowired
    public LineController(LineService lineService,SectionService sectionService){
        this.lineService=lineService;
        this.sectionService=sectionService;
    }

    @PostMapping(value = "/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest){
        if(!lineService.insertLine(new Line(lineRequest))){
            return ResponseEntity.badRequest().build();
        }
        Line newLine=lineService.findLineByName(lineRequest.getName());
        sectionService.insertFirstSection(new Section(newLine.getId(),newLine.getUpStationId(), newLine.getDownStationId(), lineRequest.getDistance()));
        List<Station> stations= sectionService.getStationsByLine(newLine);
        return ResponseEntity.created(
                URI.create("/line/" +newLine.getId()))
                .body(new LineResponse(newLine, stations));
    }

    @GetMapping("/lines")
    public ResponseEntity<List<LineResponse>> getLines(){
        // sectionService.getStationListBySectionList(sectionService.getSectionListByLineId(newLine.getId()),newLine.getUpStationId())
        List<LineResponse> response = lineService.findAll()
                .stream()
                .map(line ->(
                        new LineResponse(line, sectionService.getStationsByLine(line))
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/lines/{lineId}")
    public ResponseEntity<LineResponse> getLine(@PathVariable Long lineId){
        Line searchedLine = lineService.findById(lineId);
        List<Station> stations= sectionService.getStationsByLine(searchedLine);
        return ResponseEntity.ok().body(new LineResponse(searchedLine,stations));
    }

    @PutMapping("/lines/{lineId}")
    public ResponseEntity<LineResponse> updateLine(@PathVariable Long lineId, @RequestBody LineRequest lineRequest){
        lineService.modifyLine(new Line(lineId,lineRequest.getName(),lineRequest.getColor(),
                lineRequest.getUpStationId(),lineRequest.getDownStationId(),lineRequest.getDistance()));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{lineId}")
    public ResponseEntity<LineResponse> deleteLine(@PathVariable Long lineId){
        lineService.deleteLine(lineId);
        return  ResponseEntity.noContent().build();
    }


}
