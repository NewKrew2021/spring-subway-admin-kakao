package subway.line;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import subway.exceptions.DuplicateLineNameException;

import java.awt.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Controller
public class LineController {

    @PostMapping(value = "/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        Line newLine;
        LineResponse lineResponse;
        try {
            newLine = LineDao.save(line);
            lineResponse = new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor());
        } catch (DuplicateLineNameException exception) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.created(URI.create("/lines/" + newLine.getId())).body(lineResponse);
    }

    @GetMapping("/lines")
    public ResponseEntity<List<LineResponse>> showLines() {
        List<Line> lines = LineDao.findAll();
        List<LineResponse> lineResponses = new ArrayList<>();
        for (Line line : lines) {
            lineResponses.add(new LineResponse(line.getId(), line.getName(), line.getColor()));
        }
        return ResponseEntity.ok().body(lineResponses);
    }

}
