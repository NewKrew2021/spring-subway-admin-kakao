package subway.section;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.line.LineDao;
import subway.line.LineResponse;
import subway.station.StationDao;

@RestController
@RequestMapping("/lines/{lineId}/sections")
public class SectionController {
    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public SectionController(LineDao lineDao, StationDao stationDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    @PostMapping()
    public ResponseEntity<LineResponse> createSection(@PathVariable Long lineId, @RequestBody SectionRequest sectionRequest) {
        sectionDao.makeSection(sectionRequest.getUpStationId(), sectionRequest.getDownStationId(),
                sectionRequest.getDistance(), lineId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping()
    public ResponseEntity<LineResponse> deleteSection(@PathVariable Long lineId, @RequestParam Long stationId) {
        sectionDao.deleteByLineIdAndStationId(lineId, stationId);
        return ResponseEntity.ok().build();
    }

}
