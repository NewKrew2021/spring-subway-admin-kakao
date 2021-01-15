package subway.section;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import subway.line.Line;
import subway.line.LineDao;
import subway.station.StationDao;
import org.springframework.web.bind.annotation.RequestParam;
import javax.websocket.server.PathParam;
import java.net.URI;

@RestController
public class SectionController {

    @PostMapping(value = "/lines/{lineId}/sections", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SectionResponse> createSection(@PathVariable Long lineId,
                                                         @RequestBody SectionRequest sectionRequest){
        Line line = LineDao.getInstance().findById(lineId);
        Section section = new Section(line.getId(),
                sectionRequest.getUpStationId(),
                sectionRequest.getDownStationId(),
                sectionRequest.getDistance());

        Section newSection = SectionDao.getInstance().save(section);
        SectionResponse sectionResponse = new SectionResponse(
                newSection.getUpStationId(),
                newSection.getDownStationId(),
                newSection.getDistance()
        );
        return ResponseEntity.ok().body(sectionResponse);
    }

    @DeleteMapping("/lines/{lineId}/sections")
    public ResponseEntity deleteStation(@PathVariable Long lineId, @RequestParam("stationId") Long stationId) {
        SectionDao.getInstance().deleteById(lineId, stationId);
        return ResponseEntity.ok().build();
    }
}
