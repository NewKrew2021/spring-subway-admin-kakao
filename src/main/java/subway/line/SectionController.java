package subway.line;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class SectionController {

    private final SectionService sectionService;

    @Autowired
    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping(value = "/lines/{id}/sections")
    public ResponseEntity<SectionResponse> createSectionOnLine(@PathVariable Long id, @RequestBody SectionRequest sectionRequest) {
        Section insertedSection = sectionService.createSectionOnLine(id,
                sectionRequest.getUpStationId(),
                sectionRequest.getDownStationId(),
                sectionRequest.getDistance());

        SectionResponse response = new SectionResponse(insertedSection);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/lines/{lineId}/sections")
    public ResponseEntity<LineResponse> deleteStationOnLine(@PathVariable Long lineId, @RequestParam("stationId") Long stationId) {
        sectionService.deleteStationOnLine(lineId, stationId);

        return ResponseEntity.noContent().build();
    }
}
