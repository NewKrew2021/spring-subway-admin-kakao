package subway.section.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.section.dto.SectionRequest;
import subway.section.dto.SectionResponse;
import subway.section.service.SectionService;

@RestController
@RequestMapping("/lines")
public class SectionController {
    private SectionService sectionService;

    public SectionController(SectionService sectionService){
        this.sectionService = sectionService;
    }

    @PostMapping(path = "/{lineId}/sections", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SectionResponse> createSection(@PathVariable Long lineId,
                                                         @RequestBody SectionRequest sectionRequest){
        sectionService.createSection(lineId, sectionRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path = "/{lineId}/sections")
    public ResponseEntity deleteSection(@PathVariable Long lineId, @RequestParam("stationId") Long stationId) {
        sectionService.deleteSection(lineId, stationId);
        return ResponseEntity.ok().build();
    }
}