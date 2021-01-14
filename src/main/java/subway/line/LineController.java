package subway.line;

import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class LineController {

    @PostMapping(value = "/lines", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        long id = 1L;
        LineResponse lineResponse = new LineResponse(id, lineRequest.getName(), lineRequest.getColor(), null);

        return ResponseEntity.created(URI.create("/lines/" + id)).body(lineResponse);
    }

}
