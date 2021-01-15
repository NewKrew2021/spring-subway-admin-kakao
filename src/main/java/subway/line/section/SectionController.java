package subway.line.section;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.line.Line;
import subway.line.LineDao;
import subway.line.LineRequest;
import subway.line.LineResponse;

import java.net.URI;
import java.util.List;

@RestController
public class SectionController {
    @PostMapping("/lines/{lineId}/sections")
    public ResponseEntity<LineResponse> createSection(@PathVariable Long lineId, @RequestBody SectionRequest sectionRequest) {
        Line line = LineDao.getInstance().getLineById(lineId);
        line.connectSection(sectionRequest.getDownStationId(), sectionRequest.getUpStationId(), sectionRequest.getDistance());

        return ResponseEntity.ok().build();
    }

}
