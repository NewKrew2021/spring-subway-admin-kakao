package subway.line;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.section.SectionDao;
import subway.section.Sections;
import subway.station.StationDao;
import subway.station.StationResponse;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class LineController {
    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public LineController(LineDao lineDao, SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        System.out.println("에러1");
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        System.out.println("에러2");
        Line newLine = lineDao.save(line);
        System.out.println("에러3");
        sectionDao.LineInitialize(newLine.getId(), lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance());
        System.out.println("에러4");
        LineResponse lineResponse = new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor(), getStationResponsesByLineId(newLine.getId()));
        System.out.println("에러5");
        return ResponseEntity.created(URI.create("/lines/" + newLine.getId())).body(lineResponse);
    }

    @GetMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        return ResponseEntity.ok().body(getLineResponses());
    }

    @DeleteMapping("/lines/{lineId}")
    public ResponseEntity deleteLine(@PathVariable Long lineId) {
        lineDao.deleteById(lineId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/lines/{lineId}")
    public ResponseEntity<LineResponse> showLine(@PathVariable Long lineId) {
        Line getLine = lineDao.findById(lineId);
        LineResponse lineResponse = new LineResponse(getLine.getId(), getLine.getName(), getLine.getColor(), getStationResponsesByLineId(getLine.getId()));
        return ResponseEntity.ok().body(lineResponse);
    }

    @PutMapping(value = "/lines/{lineId}")
    public ResponseEntity putLine(@PathVariable Long lineId, @RequestBody LineRequest lineRequest) {
        lineDao.update(new Line(lineId, lineRequest.getName(), lineRequest.getColor()));
        return ResponseEntity.ok().build();
    }

    public List<LineResponse> getLineResponses() {
        return lineDao.findAll().stream()
                .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor(), getStationResponsesByLineId(line.getId())))
                .collect(Collectors.toList());
    }

    public List<StationResponse> getStationResponsesByLineId(Long lineId) {
        System.out.println("에러 분기 1");
        Sections sectionsByLineId = new Sections(sectionDao.findByLineId(lineId));
        System.out.println("에러 분기 2");
        List<Long> stationIdsByDistance = sectionsByLineId.getSortedStationIdsByDistance();
        System.out.println("에러 분기 3");
        return stationIdsByDistance.stream().map(stationId ->
                new StationResponse(stationId, stationDao.findById(stationId).getName()))
                .collect(Collectors.toList());
    }

}

