package subway.section;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.exceptions.InvalidValueException;
import subway.line.Line;
import subway.line.LineDao;

@RestController
public class SectionController {

    @Autowired
    SectionDao sectionDao;

    @Autowired
    LineDao lineDao;

    @PostMapping(value = "/lines/{lineId}/sections", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SectionResponse> createSection(@PathVariable Long lineId,
                                                         @RequestBody SectionRequest sectionRequest){
        Line line = lineDao.findById(lineId);
        Section section = new Section(line.getId(),
                sectionRequest.getUpStationId(),
                sectionRequest.getDownStationId(),
                sectionRequest.getDistance());

        Section newSection = sectionDao.save(section);
        SectionResponse sectionResponse = new SectionResponse(
                newSection.getUpStationId(),
                newSection.getDownStationId(),
                newSection.getDistance()
        );
        return ResponseEntity.ok().body(sectionResponse);
    }

    @DeleteMapping("/lines/{lineId}/sections")
    public ResponseEntity deleteStation(@PathVariable Long lineId, @RequestParam("stationId") Long stationId) {
        sectionDao.deleteById(lineId, stationId);
        return ResponseEntity.ok().build();
    }
}
