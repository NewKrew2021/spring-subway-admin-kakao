package subway.line;

import org.springframework.dao.IncorrectUpdateSemanticsDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.exception.LineNotFoundException;
import subway.exception.StationNotFoundException;
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
    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public LineController(LineDao lineDao, StationDao stationDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        if (lineDao.existBy(lineRequest.getName())) {
            throw new IllegalArgumentException("이미 등록된 지하철 노선 입니다.");
        }

        Line newLine = lineDao.save(new Line(lineRequest.getName(), lineRequest.getColor()));

        List<Section> sections = SectionFactory.createInitialSections(
                newLine.getId(),
                lineRequest.getUpStationId(),
                lineRequest.getDownStationId(),
                lineRequest.getDistance()
        );

        for (Section section : sections) {
            sectionDao.save(section);
        }

        return ResponseEntity.created(URI.create("/lines/" + newLine.getId()))
                .body(new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor(), Collections.emptyList()));
    }

    @GetMapping(value = "/lines/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        Line line = lineDao.findById(id).orElseThrow(() -> new LineNotFoundException(id));
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
        try {
            lineDao.update(line);
        } catch (IncorrectUpdateSemanticsDataAccessException e) {
            throw new LineNotFoundException(line.getId());
        }
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

        Section newSection = new Section(
                lineId,
                sectionRequest.getUpStationId(),
                sectionRequest.getDownStationId(),
                sectionRequest.getDistance()
        );

        Section insertableSection = findInsertableSection(sections, newSection);
        Section residualSection = insertableSection.subtractWith(newSection);

        sectionDao.delete(insertableSection);
        sectionDao.save(newSection);
        sectionDao.save(residualSection);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{lineId}/sections")
    public ResponseEntity<Void> deleteSections(@PathVariable Long lineId, @RequestParam Long stationId) {
        List<Section> sections = sectionDao.findByLineId(lineId);
        if (sections.size() == 3) {
            throw new IllegalStateException("해당 노선은 지하철역을 삭제할 수 없습니다.");
        }

        List<Section> connectedSections = findSectionsByStationId(sections, stationId);
        if (connectedSections.isEmpty()) {
            throw new IllegalArgumentException("노선에 포함되지 않은 지하철역 입니다.");
        }

        Section first = connectedSections.get(0);
        Section second = connectedSections.get(1);
        Section joinedSection = first.mergeWith(second);

        sectionDao.delete(first);
        sectionDao.delete(second);
        sectionDao.save(joinedSection);
        return ResponseEntity.ok().build();
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
            Long id = curr.getId();
            stations.add(
                    stationDao.findById(curr.getDownStationId())
                            .orElseThrow(() -> new StationNotFoundException(id))
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

    private List<Section> findSectionsByStationId(List<Section> sections, Long stationId) {
        return sections.stream()
                .filter(section -> section.containsStation(stationId))
                .collect(Collectors.toList());
    }
}
