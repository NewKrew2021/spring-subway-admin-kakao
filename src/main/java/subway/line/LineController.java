package subway.line;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import subway.station.Station;
import subway.station.StationDao;
import subway.station.StationResponse;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class LineController {

    @PostMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Line line = new Line(lineRequest.getColor(),
                lineRequest.getName(),
                lineRequest.getUpStationId(),
                lineRequest.getDownStationId(),
                lineRequest.getDistance());
        if(LineDao.getInstance().findAll().stream().anyMatch((Line lineSaved) -> lineSaved.equals(line))){
            return ResponseEntity.badRequest().build();
        }
        Line newline = LineDao.getInstance().save(line);
        LineResponse lineResponse = new LineResponse(newline.getId(),
                newline.getName(),
                newline.getColor(),
                newline.getStationInfo().stream()
                        .map(val -> new StationResponse(val, StationDao.getInstance().findById(val).getName())).collect(Collectors.toList()));
        return ResponseEntity.created(URI.create(("/lines/" + newline.getId()))).body(lineResponse);
    }

    @GetMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        return ResponseEntity.ok().body(LineDao.getInstance().findAll().stream()
                .map((Line line) -> new LineResponse(line))
                .collect(Collectors.toList()));
    }
}
