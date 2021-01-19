package subway.line;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.exceptions.NotFoundException;
import subway.section.Section;
import subway.section.SectionDao;
import subway.section.SectionRequest;
import subway.station.Station;
import subway.station.StationDao;
import subway.station.StationResponse;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class LineController {
    StationDao stationDao;
    LineDao lineDao;
    SectionDao sectionDao;

    public LineController(StationDao stationDao, LineDao lineDao, SectionDao sectionDao) {
        this.stationDao = stationDao;
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Section.validationCheck(lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance());

        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        Line newLine = lineDao.save(line);

        Section section = new Section(lineRequest.getUpStationId(), lineRequest.getDownStationId(), newLine.getId(), lineRequest.getDistance());
        sectionDao.save(section);

        LineResponse lineResponse = new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor());
        return ResponseEntity.created(URI.create("/lines/" + newLine.getId())).body(lineResponse);
    }

    @PostMapping("/lines/{lineId}/sections")
    public ResponseEntity createSection(@RequestBody SectionRequest sectionRequest,
                                        @PathVariable Long lineId) {
        lineDao.findById(lineId);

        Section section = new Section(sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), lineId, sectionRequest.getDistance());
        sectionDao.save(section);

        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<LineResponse> lineResponses =
                lineDao.findAll()
                        .stream()
                        .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor()))
                        .collect(Collectors.toList());

        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping(value = "/lines/{id}")
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        Line line = lineDao.findById(id);

        List<StationResponse> stationResponses = new ArrayList<>();
        List<Long> stationIds = sectionDao.findSortedStationIdsByLineId(id);

        for(Long stationId : stationIds){
            Station station = stationDao.findById(stationId);
            stationResponses.add(new StationResponse(station));
        }

        LineResponse lineResponse = new LineResponse(
                line.getId(),
                line.getName(),
                line.getColor(),
                stationResponses
        );

        return ResponseEntity.ok().body(lineResponse);
    }

    @PutMapping(value = "/lines/{id}")
    public ResponseEntity modifyLine(@RequestBody LineRequest lineRequest,
                                     @PathVariable Long id) {
        lineDao.update(new Line(id, lineRequest.getName(), lineRequest.getColor()));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/lines/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        lineDao.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/lines/{lineId}/sections")
    public ResponseEntity deleteSection(@PathVariable Long lineId,
                                        @RequestParam Long stationId) {

        sectionDao.deleteStation(lineId, stationId);
        return ResponseEntity.ok().build();
    }
}
