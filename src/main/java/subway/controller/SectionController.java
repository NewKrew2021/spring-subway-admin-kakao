package subway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.SectionRequest;
import subway.service.LineService;
import subway.service.SectionService;

@RestController
public class SectionController {

    private final SectionService sectionService;
    private final LineService lineService;

    @Autowired
    public SectionController(SectionService sectionService,LineService lineService){
        this.sectionService=sectionService;
        this.lineService=lineService;
    }

    @PostMapping("/lines/{lineId}/sections")
    public ResponseEntity<Void> createSection(@PathVariable Long lineId, @RequestBody SectionRequest sectionRequest) throws Exception {
        Line nowLine = lineService.findById(lineId);
        Section newSection = new Section(lineId, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());
        sectionService.insertSection(nowLine, newSection);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{lineId}/sections")
    public ResponseEntity<Void> deleteStation(@PathVariable("lineId") Long lineId, @RequestParam("stationId") Long stationId) throws Exception {
        Line nowLine = lineService.findById(lineId);
        sectionService.deleteStation(nowLine, stationId);
        return ResponseEntity.ok().build();
    }


}
