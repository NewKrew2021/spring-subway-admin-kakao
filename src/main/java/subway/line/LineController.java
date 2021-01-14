package subway.line;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping("/lines")
    public ResponseEntity<List<LineResponse>> showStationsOfLine(){
        List<LineResponse> lineResponses = new ArrayList<>();
        for (Line line : lineDao.findAll()) {
            lineResponses.add(new LineResponse(
                    line.getId(),
                    line.getName(),
                    line.getColor(),
                    Collections.emptyList()
            ));
        }

        return ResponseEntity.ok().body(lineResponses);
    }
}
