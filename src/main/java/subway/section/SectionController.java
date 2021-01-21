package subway.section;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.line.Line;
import subway.line.LineDao;
import subway.line.LineResponse;

@RestController
@RequestMapping("/lines/{lineId}")
public class SectionController {

    LineDao lineDao;
    SectionDao sectionDao;
    SectionService sectionService;

    @Autowired
    public SectionController(LineDao lineDao, SectionDao sectionDao, SectionService sectionService) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.sectionService = sectionService;
    }

    @PostMapping("/sections")
    public ResponseEntity<LineResponse> createLineSection(@RequestBody SectionRequest sectionRequest, @PathVariable Long lineId) {
        sectionService.addSectionToLine(Section.of(lineId, sectionRequest));
        return ResponseEntity.ok().body(LineResponse.of(lineDao.findById(lineId)));
    }

    @DeleteMapping("/sections")
    public ResponseEntity<LineResponse> deleteLineSection(@PathVariable Long lineId, @RequestParam Long stationId) {
        Line line = lineDao.findById(lineId);
        sectionService.deleteSection(line.getId(), stationId);

        return ResponseEntity.ok().body(LineResponse.of(line));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity exceptionHandler(Exception exception) {
        exception.printStackTrace();

        return ResponseEntity.badRequest().build();
    }
}
