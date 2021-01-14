package subway.line;

import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import subway.exceptions.DuplicateLineNameException;
import subway.exceptions.DuplicateStationNameException;
import subway.exceptions.InvalidLineArgumentException;
import subway.exceptions.InvalidSectionException;
import subway.station.StationDao;
import subway.station.StationResponse;

import java.awt.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class LineController {

    @ExceptionHandler(InvalidSectionException.class)
    public ResponseEntity<String> internalServerErrorHandler(InvalidSectionException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    @ExceptionHandler(InvalidLineArgumentException.class)
    public ResponseEntity<String> badRequestErrorHandler(InvalidLineArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @PostMapping(value = "/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        if (lineRequest.getDownStationId() == null || lineRequest.getUpStationId() == null || lineRequest.getDistance() == 0) {
            throw new InvalidLineArgumentException("모든 정보를 입력해주세요.");
        }
        if (lineRequest.getDownStationId() == lineRequest.getUpStationId()) {
            throw new InvalidLineArgumentException("상행종점과 하행종점은 같을 수 없습니다.");
        }
        Line line = new Line(lineRequest);
        Line newLine = LineDao.save(line);

        List<StationResponse> stationResponses = new ArrayList<>();
        List<Section> sections = newLine.getSections();
        for (int i = 0; i < sections.size() ; i++) {
            Long stationId = sections.get(i).getUpStationId();
            stationResponses.add(new StationResponse(StationDao.findById(stationId).get()));
            if(i == sections.size() - 1) {
                Long downStationId = sections.get(i).getDownStationId();
                stationResponses.add(new StationResponse(StationDao.findById(downStationId).get()));
            }
        }
        LineResponse lineResponse = new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor(), stationResponses);
        return ResponseEntity.created(URI.create("/lines/" + newLine.getId())).body(lineResponse);
    }

    @GetMapping("/lines/{lineId}")
    public ResponseEntity<LineResponse> showLine(@PathVariable(name = "lineId") Long id) {
        Optional<Line> line = LineDao.findById(id);
        Line showLine = line.get();
        if (showLine == null) {
            return ResponseEntity.badRequest().build();
        }
        List<StationResponse> stationResponses = new ArrayList<>();
        List<Section> sections = showLine.getSections();
        for (int i = 0; i < sections.size() ; i++) {
            Long stationId = sections.get(i).getUpStationId();
            stationResponses.add(new StationResponse(StationDao.findById(stationId).get()));
            if(i == sections.size() - 1) {
                Long downStationId = sections.get(i).getDownStationId();
                stationResponses.add(new StationResponse(StationDao.findById(downStationId).get()));
            }
        }
        LineResponse lineResponse = new LineResponse(showLine.getId(), showLine.getName(), showLine.getColor(), stationResponses);
        return ResponseEntity.ok().body(lineResponse);
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

    @PutMapping("/lines/{id}")
    public ResponseEntity<LineResponse> updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        Line newLine = new Line(lineRequest.getName(), lineRequest.getColor());
        LineDao.updateLine(id, newLine);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity<LineResponse> deleteLine(@PathVariable Long id) {
        boolean isLineDeleted = LineDao.deleteById(id);
        if (isLineDeleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/lines/{lineId}/sections")
    public ResponseEntity createSection(@PathVariable(name = "lineId") Long id, @RequestBody SectionRequest sectionRequest) {
        Section section = new Section(sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());
        Line line = LineDao.saveSection(id, section);
        return ResponseEntity.ok().build();
    }

//    @PostMapping("/lines/{lineId}/sections")
//    public ResponseEntity createSection(@PathVariable(name = "lineId") Long id, @RequestBody SectionRequest sectionRequest) {
//        Line line = LineDao.saveSection(id, sectionRequest);
//        List<StationResponse> stationResponses = new ArrayList<>();
//        List<Section> sections = line.getSections();
//        for (int i = 0; i < sections.size() ; i++) {
//            Long stationId = sections.get(i).getUpStationId();
//            stationResponses.add(new StationResponse(StationDao.findById(stationId).get()));
//            if(i == sections.size() - 1) {
//                Long downStationId = sections.get(i).getDownStationId();
//                stationResponses.add(new StationResponse(StationDao.findById(downStationId).get()));
//            }
//        }
//        LineResponse lineResponse = new LineResponse(line.getId(), line.getName(), line.getColor(), stationResponses);
//        return ResponseEntity.ok().body(lineResponse);
//    }
}
