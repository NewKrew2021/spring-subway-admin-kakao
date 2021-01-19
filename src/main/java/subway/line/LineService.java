package subway.line;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.URI;
import java.util.List;

@Service
public class LineService {

    @Resource
    private LineDao lineDao;
    @Resource
    private SectionDao sectionDao;

    public ResponseEntity<LineResponse> create(LineRequest lineRequest) {
        try {
            lineDao.save(new Line(lineRequest));
        } catch (DuplicateKeyException e) {
            return ResponseEntity.badRequest().build();
        }

        Line newLine = lineDao.findByName(lineRequest.getName());
        sectionDao.save(new Section(newLine));

        return ResponseEntity.created(URI.create("/lines/" + newLine.getId()))
                .body(new LineResponse(newLine, getStations(newLine.getId())));
    }

    public ResponseEntity<List<LineResponse>> getLines() {
        List<Line> lines = lineDao.findAll();

        return ResponseEntity.ok().body(LineResponse.getLineResponses(lines));
    }

    public ResponseEntity delete(Long id) {
        lineDao.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<LineResponse> getLine(Long id) {
        Line line = lineDao.findById(id);

        return ResponseEntity.ok(new LineResponse(line, stationService.getStations(id)));
    }

    public ResponseEntity update(Long id, LineRequest lineRequest) {
        lineDao.update(new Line(id, lineRequest));

        return ResponseEntity.ok().build();
    }
}
