package subway.section;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lines/{lineId}/sections")
public class SectionController {
    private SectionService sectionService;

    public SectionController(SectionService sectionService){
        this.sectionService = sectionService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SectionResponse> createSection(@PathVariable Long lineId,
                                                         @RequestBody SectionRequest sectionRequest){
        SectionResponse sectionResponse = sectionService.createSection(lineId, sectionRequest);
        return ResponseEntity.ok().body(sectionResponse);
    }

    @DeleteMapping()
    public ResponseEntity deleteSection(@PathVariable Long lineId, @RequestParam("stationId") Long stationId) {
        sectionService.deleteSection(lineId, stationId);
        return ResponseEntity.ok().build();
    }
}
