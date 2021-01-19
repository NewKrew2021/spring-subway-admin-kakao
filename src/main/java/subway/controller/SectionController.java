package subway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.dto.SectionRequest;
import subway.service.SectionService;

@RestController
@RequestMapping("/lines")
public class SectionController {
    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping("/{lineId}/sections")
    public ResponseEntity createSection(@PathVariable Long lineId, @RequestBody SectionRequest sectionRequest) {
        if (!sectionService.saveSectionAsRequest(sectionRequest, lineId)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{lineId}/sections")
    public ResponseEntity deleteSection(@PathVariable Long lineId, @RequestParam Long stationId) {
        if (!sectionService.deleteSection(lineId, stationId)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok().build();
    }
}
