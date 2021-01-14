package subway.line;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import subway.station.StationResponse;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
public class LineController {

    private static final LineDao lineDao = new LineDao();

    @PostMapping(value = "/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Optional<Line> byName = lineDao.findByName(lineRequest.getName());
        if (byName.isPresent()){
            return ResponseEntity.badRequest().build();
        }
        Line line = lineDao.save(new Line(lineRequest.getColor(), lineRequest.getName()));
        LineResponse lineResponse = new LineResponse(
                line.getId(),
                line.getName(),
                line.getColor(),
                Collections.emptyList()
        );
        return ResponseEntity.created(URI.create("/stations/" + line.getId())).body(lineResponse);
    }
}
