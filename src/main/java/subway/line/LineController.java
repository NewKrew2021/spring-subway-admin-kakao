package subway.line;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.NotFoundException;
import subway.section.Section;
import subway.section.SectionDao;
import subway.section.SectionRequest;
import subway.station.Station;
import subway.station.StationDao;
import subway.station.StationResponse;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class LineController {
    LineDao lineDao = new LineDao();

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest){
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        Line newLine = lineDao.save(line);
        LineResponse lineResponse = new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor());

        return ResponseEntity.created(URI.create("/lines/" + newLine.getId())).body(lineResponse);
    }

    @GetMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<LineResponse> lineResponses =
                lineDao.findAll()
                        .stream()
                        .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor()))
                        .collect(Collectors.toList());

        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping(value = "/lines/{id}")
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id){
        Line line = lineDao.findById(id).orElseThrow(() -> new NotFoundException());

        return ResponseEntity.ok().body(lineResponse);
    }

    @PutMapping(value = "/lines/{id}")
    public ResponseEntity modifyLine(@RequestBody LineRequest lineRequest,
                                                   @PathVariable Long id){
        lineDao.update(new Line(lineRequest.getName(), lineRequest.getColor(), id));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/lines/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        lineDao.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
