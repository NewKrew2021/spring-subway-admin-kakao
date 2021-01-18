package subway.line;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.station.Station;
import subway.station.StationDao;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class LineController {
    private LineDao lineDao;
    private StationDao stationDao;

    public LineController(){
        lineDao = LineDao.getInstance();
        stationDao = StationDao.getInstance();
    }

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createStation(@RequestBody LineRequest lineRequest) {
        List<Station> stations = new LinkedList<>(
                Arrays.asList(stationDao.findById(lineRequest.getUpStationId()),
                        stationDao.findById(lineRequest.getDownStationId())));
        Line line = new Line(lineRequest.getName(), lineRequest.getColor(), lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance(), stations);
        Line newLine = lineDao.save(line);
        if (newLine == null) {
            return ResponseEntity.badRequest().build();
        }
        LineResponse lineResponse = new LineResponse(newLine.getId(), newLine.getColor(), newLine.getName());
        return ResponseEntity.created(URI.create("/lines/" + newLine.getId())).body(lineResponse);
    }

    @GetMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<LineResponse> responses = lineDao.findAll().stream()
                .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/lines/{lineId}")
    public ResponseEntity<LineResponse> showLine(@PathVariable Long lineId) {
        Line line = lineDao.findById(lineId);
        if (line == null) {
            System.out.println("문제 발생");
            return ResponseEntity.badRequest().build();
        }
        LineResponse lineResponse = new LineResponse(line.getId(), line.getName(), line.getColor());
        return ResponseEntity.ok(lineResponse);
    }

    @PutMapping("/lines/{id}")
    public ResponseEntity<LineResponse> updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest){
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        lineDao.updateById(id, line);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity deleteStation(@PathVariable Long id) {
        lineDao.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/lines/{lineId}/sections")
    public ResponseEntity<LineResponse> createSection(@PathVariable Long lineId, @RequestBody SectionRequest sectionRequest) {
        Section section = new Section(lineId, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());
        try {
            lineDao.updateStation(section);
        } catch(IllegalArgumentException illegalArgumentException) {
            System.out.println("섹션 문제 발");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok().build();
    }

}
