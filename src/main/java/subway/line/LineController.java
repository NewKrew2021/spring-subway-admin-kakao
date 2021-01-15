package subway.line;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.section.Section;
import subway.section.SectionDao;
import subway.section.SectionFactory;
import subway.section.SectionRequest;
import subway.station.Station;
import subway.station.StationDao;
import subway.station.StationResponse;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
public class LineController {
    private final LineDao lineDao = new LineDao();
    private final StationDao stationDao = StationDao.getInstance();
    private final SectionDao sectionDao = new SectionDao();

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(),
                lineRequest.getColor());
        Line newLine = lineDao.save(line);

        List<Section> sections = SectionFactory.createInitialSections(newLine.getId(),
                lineRequest.getUpStationId(),
                lineRequest.getDownStationId(),
                lineRequest.getDistance());

        for (Section section : sections) {
            sectionDao.save(section);
        }

        return ResponseEntity.created(URI.create("/lines/" + newLine.getId()))
                .body(new LineResponse(line.getId(), line.getName(), line.getColor(), Collections.emptyList()));
    }

    @GetMapping(value = "/lines/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        Line line = lineDao.find(id).orElseThrow(() -> new IllegalArgumentException("노선이 존재하지 않습니다."));
        List<Station> stations = getStationsByLine(line);

        List<StationResponse> stationResponses = stations.stream()
                .map(station -> new StationResponse(station.getId(), station.getName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(new LineResponse(line.getId(), line.getName(), line.getColor(), stationResponses));
    }

    @GetMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        return lineDao.findAll()
                .stream()
                .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor(), Collections.emptyList()))
                .collect(Collectors.collectingAndThen(Collectors.toList(), ResponseEntity::ok));
    }

    @PutMapping(value = "/lines/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateLine(@RequestBody LineRequest lineRequest, @PathVariable Long id) {
        Line line = new Line(id, lineRequest.getName(), lineRequest.getColor());
        lineDao.update(line);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineDao.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/lines/{lineId}/sections")
    public ResponseEntity<Void> createSection(@PathVariable Long lineId, @RequestBody SectionRequest sectionRequest) {
        List<Section> sections = sectionDao.findByLineId(lineId);

        Section newSection = new Section(lineId,
                sectionRequest.getUpStationId(),
                sectionRequest.getDownStationId(),
                sectionRequest.getDistance());

        Section insertableSection = findInsertableSection(sections, newSection);
        Section residualSection = insertableSection.getDifferenceSection(newSection);

        sectionDao.delete(insertableSection);
        sectionDao.save(newSection);
        sectionDao.save(residualSection);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalStateException(IllegalStateException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    private List<Station> getStationsByLine(Line line) {
        List<Section> sections = sectionDao.findByLineId(line.getId());
        Map<Long, Section> sectionCache = sections.stream()
                .collect(Collectors.toMap(Section::getUpStationId, Function.identity()));

        Section curr = sections.stream()
                .filter(Section::isUpTerminal)
                .findFirst()
                .orElseThrow(AssertionError::new);

        List<Station> stations = new ArrayList<>();
        while (!curr.isDownTerminal()) {
            stations.add(
                    stationDao.findById(curr.getDownStationId()).get()
            );
            curr = sectionCache.get(curr.getDownStationId());
        }
        return stations;
    }

    private Section findInsertableSection(List<Section> sections, Section newSection) {
        return sections.stream()
                .filter(section -> section.hasSameUpStation(newSection)
                        || section.hasSameDownStation(newSection))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("삽입할 수 있는 구간이 없습니다."));
    }
}
