package subway.line;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.exception.NotFoundException;
import subway.section.Section;
import subway.section.SectionRequest;
import subway.section.SectionService;
import subway.station.Station;
import subway.station.StationDao;
import subway.station.StationResponse;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/lines")
@RestController
public class LineController {
    SectionService sectionService;
    LineDao lineDao;
    StationDao stationDao;

    public LineController(StationDao stationDao, LineDao lineDao, SectionService sectionService) {
        this.stationDao = stationDao;
        this.lineDao = lineDao;
        this.sectionService = sectionService;
    }

    @PostMapping("")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Line line = lineRequest.mapToLine();

        Line newLine = lineDao.save(line);
        Station upStation = stationDao.findById(lineRequest.getUpStationId()).orElseThrow(NotFoundException::new);
        Station downStation = stationDao.findById(lineRequest.getDownStationId()).orElseThrow(NotFoundException::new);

        Section section = new Section(newLine, upStation, downStation, lineRequest.getDistance());
        sectionService.save(section);

        LineResponse lineResponse = new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor());

        return ResponseEntity.created(URI.create("/lines/" + newLine.getId())).body(lineResponse);
    }

    @PostMapping("/{lineId}/sections")
    public ResponseEntity createSection(@RequestBody SectionRequest sectionRequest,
                                        @PathVariable Long lineId) {
        Line line = lineDao.findById(lineId).orElseThrow(NotFoundException::new);
        Station upStation = stationDao.findById(sectionRequest.getUpStationId()).orElseThrow(NotFoundException::new);
        Station downStation = stationDao.findById(sectionRequest.getDownStationId()).orElseThrow(NotFoundException::new);

        Section section = new Section(line, upStation, downStation, sectionRequest.getDistance());
        sectionService.save(section);

        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<LineResponse> lineResponses =
                lineDao.findAll()
                        .stream()
                        .map(Line::mapToResponse)
                        .collect(Collectors.toList());

        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        Line line = lineDao.findById(id).orElseThrow(() -> new NotFoundException());

        List<StationResponse> stationResponses = sectionService.findSortedStationsByLine(line)
                .stream()
                .map(Station::mapToResponse)
                .collect(Collectors.toList());

        LineResponse lineResponse = line.mapToResponse(stationResponses);

        return ResponseEntity.ok().body(lineResponse);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity modifyLine(@RequestBody LineRequest lineRequest,
                                     @PathVariable Long id) {
        lineDao.update(new Line(id, lineRequest.getName(), lineRequest.getColor()));

        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        lineDao.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/{lineId}/sections")
    public ResponseEntity deleteSection(@PathVariable Long lineId,
                                        @RequestParam Long stationId) {
        Station station = stationDao.findById(stationId).orElseThrow(NotFoundException::new);
        Line line = lineDao.findById(lineId).orElseThrow(NotFoundException::new);

        sectionService.deleteStation(line, station);

        return ResponseEntity.ok().build();
    }
}
