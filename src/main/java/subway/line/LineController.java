package subway.line;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static subway.Container.*;

@RestController
public class LineController {


    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        if (lineDao.existName(lineRequest.getName())) {
            return ResponseEntity.badRequest().build();
        }
        List<StationResponse> stations = new ArrayList<>();
        try {
            Station upStation = stationDao.findById(lineRequest.getUpStationId());
            Station downStation = stationDao.findById(lineRequest.getDownStationId());
            stations.add(new StationResponse(upStation.getId(), upStation.getName()));
            stations.add(new StationResponse(downStation.getId(), downStation.getName()));
        } catch (NotExistException e) {
            return ResponseEntity.badRequest().build();
        }
        Line line = new Line(lineRequest.getName(), lineRequest.getColor(), lineRequest.getUpStationId());
        Line newLine = lineDao.save(line);
        Section section = new Section(lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance());
        sectionDao.save(section);
        LineResponse lineResponse = new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor(), stations);
        return ResponseEntity.created(URI.create("/lines/" + newLine.getId())).body(lineResponse);
    }

    @GetMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<Line> lines = lineDao.findAll();
        List<LineResponse> lineResponses = new ArrayList<>();
        for (Line line : lines) {
            lineResponses.add(new LineResponse(line.getId(), line.getName(), line.getColor(), null));
        }
        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping(value = "/lines/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLines(@PathVariable long id) {
        Line line = lineDao.findById(id);
        return ResponseEntity.ok().body(new LineResponse(line.getId(), line.getName(), line.getColor(), null));
    }

//    @RequestMapping(value = "/lines/{id}", method = RequestMethod.PUT)
//    public ResponseEntity updateLine(@PathVariable long id, @RequestBody LineRequest lineRequest) {
//
//        return ResponseEntity.ok().body(lineResponses);
//    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        lineDao.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
