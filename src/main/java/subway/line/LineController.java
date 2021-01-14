package subway.line;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.section.Section;
import subway.section.SectionDao;
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
                lineRequest.getColor(),
                lineRequest.getUpStationId(),
                lineRequest.getDownStationId());
        Line newLine = lineDao.save(line);

        Section newSection = new Section(newLine.getId(),
                lineRequest.getUpStationId(),
                lineRequest.getDownStationId(),
                lineRequest.getDistance());
        sectionDao.save(newSection);

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
        Line line = new Line(id, lineRequest.getName(), lineRequest.getColor(), lineRequest.getUpStationId(), lineRequest.getDownStationId());
        lineDao.update(line);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineDao.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private Section findInsertableSection(List<Section> sections, Section newSection) {
        return sections.stream()
                .filter(section -> section.getUpStationId() == newSection.getUpStationId()
                        || section.getDownStationId() == newSection.getDownStationId())
                .findAny()
                .orElse(null);
//                .orElseThrow(() -> new RuntimeException("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음"));
    }

    private Section createResidualSection(Long lineId, Section findSection, Section newSection) {
        if (newSection.getUpStationId() == findSection.getUpStationId()) {
            return new Section(lineId,
                    newSection.getDownStationId(),
                    findSection.getDownStationId(),
                    findSection.getDistance() - newSection.getDistance());
        }
        return new Section(lineId,
                findSection.getUpStationId(),
                newSection.getUpStationId(),
                findSection.getDistance() - newSection.getDistance());
    }

    private Section findExtendableSection(Line line, List<Section> sections, Section newSection) {
        return sections.stream()
                .filter(section -> section.getUpStationId() == line.getUpTerminalStationId()
                        || section.getDownStationId() == line.getDownTerminalStationId())
                .filter(section -> section.getUpStationId() == newSection.getDownStationId()
                        || section.getDownStationId() == newSection.getUpStationId())
                .findAny()
                .orElse(null);
    }

    @PostMapping("/lines/{lineId}/sections")
    public ResponseEntity<Void> createSection(@PathVariable Long lineId, @RequestBody SectionRequest sectionRequest) {
        Line line = lineDao.find(lineId).get();
        List<Section> sections = sectionDao.findByLineId(lineId);

        Section newSection = new Section(lineId,
                sectionRequest.getUpStationId(),
                sectionRequest.getDownStationId(),
                sectionRequest.getDistance());

        Section findSection = findInsertableSection(sections, newSection);
        if (findSection != null) {
            Section residualSection = createResidualSection(lineId, findSection, newSection);
            sectionDao.delete(findSection);
            sectionDao.save(newSection);
            sectionDao.save(residualSection);
        } else {
            findSection = findExtendableSection(line, sections, newSection);
            Line newLine = null;
            if (findSection.getUpStationId() == newSection.getDownStationId()) {
                newLine = new Line(lineId,
                        line.getName(),
                        line.getColor(),
                        newSection.getUpStationId(),
                        line.getDownTerminalStationId());

            } else {
                newLine = new Line(lineId,
                        line.getName(),
                        line.getColor(),
                        line.getUpTerminalStationId(),
                        newSection.getDownStationId());
            }
            lineDao.update(newLine);
            sectionDao.save(newSection);
        }
        // 3. 기존 역사의 길이보다 같거나 길면 거
        // 4. 중복 (상/하행 둘다 등록되어있으면 거절)
        // 5. 상/하행 둘다 처음보면 거(ㅎ해결?)
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalStateException(IllegalStateException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.notFound().build();
    }

    private List<Station> getStationsByLine(Line line) {
        List<Section> sections = sectionDao.findByLineId(line.getId());
        Map<Long, Section> sectionCache = sections.stream()
                .collect(Collectors.toMap(Section::getUpStationId, Function.identity()));

        Section curr = sections.stream()
                .filter(section -> section.getUpStationId() == line.getUpTerminalStationId())
                .findFirst()
                .orElse(null);

        List<Station> stations = new ArrayList<>();
        stations.add(stationDao.findById(curr.getUpStationId()).get());
        while (curr != null) {
            stations.add(
                    stationDao.findById(curr.getDownStationId()).get()
            );
            curr = sectionCache.getOrDefault(curr.getDownStationId(), null);
        }
        return stations;
    }
}
