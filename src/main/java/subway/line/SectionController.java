package subway.line;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lines")
public class SectionController {
    private final LineDao lineDao;

    public SectionController(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    @PostMapping("/{lineId}/sections")
    public ResponseEntity createSection(@PathVariable Long lineId, @RequestBody SectionRequest sectionRequest) {
        Section section = new Section(sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());
        if (!lineDao.saveSection(lineId, section)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{lineId}/sections")
    public ResponseEntity deleteSection(@PathVariable Long lineId, @RequestParam Long stationId) {
        if (!lineDao.deleteSection(lineId, stationId)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok().build();
    }
}
