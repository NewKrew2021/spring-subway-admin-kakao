package subway.line;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.station.Station;
import subway.station.StationDao;
import subway.station.StationResponse;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class LineController {
    @Autowired
    private StationDao stationDao;

    @Autowired
    private LineDao lineDao;

    @Autowired
    private SectionDao sectionDao;

    @DeleteMapping("/lines/{lineId}/sections")
    public ResponseEntity<LineResponse> removeLineSection(@PathVariable Long lineId, @RequestParam Long stationId) {
        Line line = lineDao.findById(lineId);
        sectionDao.removeSection(stationId);

        LineResponse lineResponse = new LineResponse(line.getId(), line.getName(), line.getColor());
        return ResponseEntity.ok().body(lineResponse);
    }


    @PostMapping("/lines/{lineId}/sections")
    public ResponseEntity<LineResponse> createLineSection(@RequestBody SectionRequest sectionRequest, @PathVariable Long lineId) {
        Section section = new Section(
                lineId, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());

        sectionDao.add(section);

        Line line = lineDao.findById(lineId);
        LineResponse lineResponse = new LineResponse(line.getId(), line.getName(), line.getColor());

        return ResponseEntity.ok().body(lineResponse);
    }

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        if (isNameDuplicate(lineRequest.getName())) {
            return ResponseEntity.badRequest().build();
        }
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        Line newLine = lineDao.save(line);

        Section section = new Section(
                newLine.getId(), lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance());
        sectionDao.save(section);

        LineResponse lineResponse = new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor());

        return ResponseEntity.created(URI.create("/lines/" + newLine.getId()))
                .body(lineResponse);
    }

    private boolean isNameDuplicate(String name) {
        return lineDao.findByName(name) != null;
    }

    @GetMapping(value = "/lines/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        Line line = lineDao.findById(id);
        List<StationResponse> stationResponses = sectionDao.getStationIds(id).stream()
                .map(stationId -> {
                    Station station = stationDao.findById(stationId);
                    return new StationResponse(station.getId(), station.getName());
                })
                .collect(Collectors.toList());
        LineResponse lineResponse = new LineResponse(line.getId(), line.getName(), line.getColor(), stationResponses);

        return ResponseEntity.ok().body(lineResponse);
    }

    @GetMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<Line> lines = lineDao.findAll();
        List<LineResponse> lineResponses = lines.stream()
                .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor()))
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(lineResponses);
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        lineDao.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/lines/{id}")
    public ResponseEntity updateLine(@RequestBody LineRequest lineRequest, @PathVariable Long id) {
        Line line = lineDao.findById(id);
        Line updateLine = new Line(lineRequest.getName(), lineRequest.getColor());
        lineDao.update(line, updateLine);
        return ResponseEntity.ok().build();
    }
}
