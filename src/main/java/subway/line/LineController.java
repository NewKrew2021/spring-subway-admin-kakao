package subway.line;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.station.StationDao;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import subway.section.Section;
import subway.section.SectionDao;


@RestController
public class LineController {
    @Autowired
    LineDao lineDao;
    @Autowired
    SectionDao sectionDao;
    @Autowired
    StationDao stationDao;


    @PostMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(),
                lineRequest.getColor(),
                lineRequest.getExtraFare());

        Line newline = lineDao.save(line, lineRequest);
        sectionDao.save(new Section(newline.getId(),
                lineRequest.getUpStationId(),
                lineRequest.getDownStationId(),
                lineRequest.getDistance()));
        LineResponse lineResponse = new LineResponse(newline, stationDao, sectionDao);
        return ResponseEntity.created(URI.create(("/lines/" + newline.getId()))).body(lineResponse);
    }

    @GetMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        return ResponseEntity.ok().body(lineDao.findAll().stream()
                .map((Line line) -> new LineResponse(line, stationDao, sectionDao))
                .collect(Collectors.toList()));
    }

    @GetMapping(value = "/lines/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id){
        return ResponseEntity.ok().body(new LineResponse(lineDao.findById(id), stationDao, sectionDao));
    }

    @PutMapping(value = "/lines/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateLine(@PathVariable Long id,@RequestBody LineRequest lineRequest){
        lineDao.update(id, lineRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity deleteStation(@PathVariable Long id) {
        lineDao.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
