package subway.line;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.station.StationDao;
import subway.station.StationResponse;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class LineController {

    private final StationDao stationDao;
    private final LineDao lineDao;
    private final SectionDao sectionDao;

    @Autowired
    public LineController(StationDao stationDao, LineDao lineDao, SectionDao sectionDao) {
        this.stationDao = stationDao;
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    @PostMapping(value = "/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        Line newLine = lineDao.save(line);

        SectionGroup sections = SectionGroup.insertFirstSection(newLine.getId(),
                lineRequest.getUpStationId(),
                lineRequest.getDownStationId(),
                lineRequest.getDistance());

        sectionDao.saveAll(sections);

        LineResponse lineResponse = new LineResponse(newLine);
        return ResponseEntity.created(URI.create("/lines/" + newLine.getId())).body(lineResponse);
    }

    @GetMapping("/lines")
    public ResponseEntity<List<LineResponse>> showAllLines() {
        List<LineResponse> responses = lineDao.findAll().stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(responses);
    }

    @GetMapping("/lines/{id}")
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        Line line = lineDao.findOne(id);
        SectionGroup sections = new SectionGroup(sectionDao.findAllByLineId(id));
        LineResponse response = new LineResponse(line,
                sections.getAllStationId().stream()
                        .map(stationDao::findOne)
                        .map(StationResponse::new)
                        .collect(Collectors.toList()));

        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/lines/{id}")
    public ResponseEntity<LineResponse> updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        Line line = new Line(id, lineRequest.getName(), lineRequest.getColor());
        lineDao.update(line);
        LineResponse response = new LineResponse(line);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity<LineResponse> deleteLine(@PathVariable Long id) {
        lineDao.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/lines/{id}/sections")
    public ResponseEntity<SectionResponse> createSectionOnLine(@PathVariable Long id, @RequestBody SectionRequest sectionRequest) {
        SectionGroup sections = new SectionGroup(sectionDao.findAllByLineId(id));
        Section insertedSection = sections.insertSection(id,
                sectionRequest.getUpStationId(),
                sectionRequest.getDownStationId(),
                sectionRequest.getDistance());
        Section dividedSection = sections.divideSection(insertedSection);

        sectionDao.save(insertedSection);
        sectionDao.update(dividedSection);

        SectionResponse response = new SectionResponse(insertedSection);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/lines/{lineId}/sections")
    public ResponseEntity<LineResponse> deleteStationOnLine(@PathVariable Long lineId, @RequestParam("stationId") Long stationId) {
        SectionGroup sections = new SectionGroup(sectionDao.findAllByLineId(lineId));
        Section deletedSection = sections.deleteStation(stationId);
        Section combinedSection = sections.combineSection(deletedSection);

        sectionDao.deleteById(deletedSection.getId());
        sectionDao.update(combinedSection);

        return ResponseEntity.noContent().build();
    }
}
