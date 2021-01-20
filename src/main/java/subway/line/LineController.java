package subway.line;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.station.StationDao;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class LineController {

    private final LineService lineService;
    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public LineController(LineService lineService,
                          LineDao lineDao,
                          StationDao stationDao,
                          SectionDao sectionDao) {
        this.lineService = lineService;
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    @PostMapping(value = "/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Line line = lineDao.save(new Line(lineRequest.getName(),
                lineRequest.getColor()
        ));
        sectionDao.save(new Section(
                line.getId(),
                stationDao.findOne(lineRequest.getUpStationId()),
                stationDao.findOne(lineRequest.getDownStationId()),
                lineRequest.getDistance()
        ));
        LineResponse lineResponse = new LineResponse(line);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(lineResponse);
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
        LineResponse response = new LineResponse(line);
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/lines/{id}")
    public ResponseEntity<LineResponse> updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        Line line = lineDao.findOne(id);
        LineResponse response = new LineResponse(lineDao.update(id, line));
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity<LineResponse> deleteLine(@PathVariable Long id) {
        lineDao.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/lines/{lineId}/sections")
    public ResponseEntity<LineResponse> deleteStationOnLine(@PathVariable Long lineId, @RequestParam("stationId") Long stationId) {
        Line line = lineDao.findOne(lineId);
        Section previous = sectionDao.findOneByLineIdAndStationId(lineId, stationId, false);
        Section next = sectionDao.findOneByLineIdAndStationId(lineId, stationId, true);
        sectionDao.update(new Section(
                previous.getId(),
                previous.getLineId(),
                previous.getUpStation(),
                next.getDownStation(),
                previous.getDistance() + next.getDistance()
        ));
        sectionDao.deleteById(next.getId());
        stationDao.deleteById(stationId);
        line.deleteStation(stationId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/lines/{lineId}/sections")
    public ResponseEntity<SectionResponse> createSectionOnLine(@PathVariable Long lineId, @RequestBody SectionRequest sectionRequest) {
        Line line = lineDao.findOne(lineId);
        Long upStationId = sectionRequest.getUpStationId();
        Long downStationId = sectionRequest.getDownStationId();
        Section section = new Section(line.getId(),
                stationDao.findOne(upStationId),
                stationDao.findOne(downStationId),
                sectionRequest.getDistance());

        lineService.checkDuplicateName(lineId, upStationId, downStationId);

        lineService.addSection(line, section);
        SectionResponse response = new SectionResponse(section);
        return ResponseEntity.ok().body(response);
    }
}
