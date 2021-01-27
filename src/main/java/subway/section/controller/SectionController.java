package subway.section.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.line.domain.Line;
import subway.line.service.LineService;
import subway.section.domain.Section;
import subway.section.dto.SectionRequest;
import subway.section.dto.SectionResponse;
import subway.section.service.SectionService;

@RestController
public class SectionController {
    private final LineService lineService;
    private final SectionService sectionService;

    public SectionController(LineService lineService, SectionService sectionService) {
        this.lineService = lineService;
        this.sectionService = sectionService;
    }

    @PostMapping(value = "/lines/{lineId}/sections", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SectionResponse> createSection(@PathVariable Long lineId,
                                                         @RequestBody SectionRequest sectionRequest) {
        Line line = lineService.findById(lineId);
        Section section = new Section(line.getId(),
                sectionRequest.getUpStationId(),
                sectionRequest.getDownStationId(),
                sectionRequest.getDistance());

        Section newSection = sectionService.save(section);
        SectionResponse sectionResponse = new SectionResponse(
                newSection.getUpStationId(),
                newSection.getDownStationId(),
                newSection.getDistance()
        );
        return ResponseEntity.ok().body(sectionResponse);
    }

    @DeleteMapping("/lines/{lineId}/sections")
    public ResponseEntity deleteStation(@PathVariable Long lineId, @RequestParam("stationId") Long stationId) {
        sectionService.deleteById(lineId, stationId);
        return ResponseEntity.ok().build();
    }
}
