package subway.line;

import org.springframework.http.ResponseEntity;
import subway.station.Station;
import subway.station.StationDao;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class LineService {

    private LineDao lineDao;
    private StationDao stationDao;

    public LineService(){
        this.lineDao = LineDao.getInstance();
        this.stationDao = StationDao.getInstance();
    }

    public ResponseEntity<LineResponse> createLine(LineRequest lineRequest) {
        Line line = new Line(lineRequest);
        Station upStation = stationDao.findById(lineRequest.getUpStationId()).get();
        Station downStation = stationDao.findById(lineRequest.getDownStationId()).get();
        line.addStation(upStation);
        line.addStation(downStation);
        Line newLine;
        try {
            newLine = lineDao.save(line);
        } catch(IllegalArgumentException iae) {
            return ResponseEntity.badRequest().build();
        }
        LineResponse lineResponse = new LineResponse(newLine.getId(),newLine.getName(),newLine.getColor(),newLine.getStations());
        return ResponseEntity.created(URI.create("/lines/" + newLine.getId())).body(lineResponse);
    }

    public ResponseEntity<List<LineResponse>> showLines() {
        List<Line> lines = lineDao.findAll();
        List<LineResponse> lineResponses = lines.stream().map(LineResponse::new).collect(Collectors.toList());
        return ResponseEntity.ok().body(lineResponses);
    }

    public ResponseEntity deleteLine(Long id) {
        lineDao.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<LineResponse> showLine(Long id) {
        Optional<Line> lineOptional = lineDao.findById(id);
        LineResponse lineResponse = new LineResponse(lineOptional.get());
        return ResponseEntity.ok(lineResponse);
    }

    public ResponseEntity updateLine(Long id, LineRequest lineRequest) {
        lineDao.update(id, lineRequest);
        return ResponseEntity.ok().build();
    }


}
