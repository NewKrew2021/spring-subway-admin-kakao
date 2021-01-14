package subway.line;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import subway.station.StationDao;
import subway.station.StationResponse;

import java.net.URI;
import java.util.stream.Collectors;

@RestController
public class LineController {

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Line line = new Line(lineRequest.getColor(),
                lineRequest.getName(),
                lineRequest.getUpStationId(),
                lineRequest.getDownStationId(),
                lineRequest.getDistance());
        Line newline = LineDao.getInstance().save(line);
        LineResponse lineResponse = new LineResponse(newline.getId(),
                newline.getName(),
                newline.getColor(),
                newline.getStationInfo().stream()
                        .map(val -> new StationResponse(val, StationDao.getInstance().findById(val).getName())).collect(Collectors.toList()));
        return ResponseEntity.created(URI.create(("/lines/" + newline.getId()))).body(lineResponse);
    }
}
