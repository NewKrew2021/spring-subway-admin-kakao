package subway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.SectionRequest;
import subway.domain.SectionResponse;
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
    public ResponseEntity<SectionResponse> createSection(@PathVariable Long lineId, @RequestBody SectionRequest sectionRequest) {
        Line nowLine = lineService.findById(lineId);
        Section newSection = new Section(lineId, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());
        if (sectionService.insertSection(nowLine, newSection)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @DeleteMapping("/lines/{lineId}/sections")
    public ResponseEntity<SectionResponse> deleteStation(@PathVariable("lineId") Long lineId, @RequestParam("stationId") Long stationId) {
        Line nowLine = lineService.findById(lineId);
        if (sectionService.deleteStation(nowLine, stationId)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }


}
