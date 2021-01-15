package subway.line;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.station.StationDao;
import subway.station.StationResponse;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import subway.section.Section;
import subway.section.SectionDao;


@RestController
public class LineController {

    @PostMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(),
                lineRequest.getColor(),
                lineRequest.getUpStationId(),
                lineRequest.getDownStationId(),
                lineRequest.getDistance(),
                lineRequest.getExtraFare());

        // 1 라인 추가
        Line newline = LineDao.getInstance().save(line);
        // 2. 섹션 추가
        SectionDao.getInstance().save(new Section(newline.getId(), newline.getUpStationId(), newline.getDownStationId(), newline.getDistance()));
        LineResponse lineResponse = new LineResponse(newline);
        return ResponseEntity.created(URI.create(("/lines/" + newline.getId()))).body(lineResponse);
    }

    @GetMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        return ResponseEntity.ok().body(LineDao.getInstance().findAll().stream()
                .map((Line line) -> new LineResponse(line))
                .collect(Collectors.toList()));
    }

    @GetMapping(value = "/lines/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id){
        return ResponseEntity.ok().body(new LineResponse(LineDao.getInstance().findById(id)));
    }

    @PutMapping(value = "/lines/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateLine(@PathVariable Long id,@RequestBody LineRequest lineRequest){
        Line line = LineDao.getInstance().findById(id);

        line.updateAll(lineRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity deleteStation(@PathVariable Long id) {
        LineDao.getInstance().deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
