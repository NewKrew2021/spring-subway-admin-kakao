package subway.line;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.dao.IncorrectUpdateSemanticsDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.exception.LineNotFoundException;
import subway.exception.StationNotFoundException;
import subway.section.*;
import subway.station.Station;
import subway.station.StationDao;
import subway.station.StationResponse;

import java.net.URI;
import java.util.Collections;
import java.util.List;
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
        Sections sections = new Sections(sectionDao.findByLineId(line.getId()));

        List<StationResponse> stationResponses = getSortedStations(sections)
                .stream()
                .map(StationResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new LineResponse(line.getId(), line.getName(), line.getColor(), stationResponses));
    }

    private List<Station> getSortedStations(Sections sections) {
        return sections.getSortedStationIds()
                .stream()
                .map(stationId -> stationDao.findById(stationId)
                        .orElseThrow(() -> new StationNotFoundException(stationId)))
                .collect(Collectors.toList());
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
        Sections sections = new Sections(sectionDao.findByLineId(lineId));

        Section newSection = new Section(
                lineId,
                sectionRequest.getUpStationId(),
                sectionRequest.getDownStationId(),
                sectionRequest.getDistance()
        );

        Section insertTargetSection = sections.findBySameUpOrDownStationWith(newSection);
        Section residualSection = insertTargetSection.subtractWith(newSection);
        sectionDao.delete(insertTargetSection);
        sectionDao.save(newSection);
        sectionDao.save(residualSection);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{lineId}/sections")
    public ResponseEntity<Void> deleteSections(@PathVariable Long lineId, @RequestParam Long stationId) {
        Sections sections = new Sections(sectionDao.findByLineId(lineId));
        if (sections.isInitialState()) {
            throw new IllegalStateException("해당 노선은 지하철역을 삭제할 수 없습니다.");
        }

        Pair<Section, Section> connectedSections = sections.findByStationId(stationId);
        Section first = connectedSections.getLeft();
        Section second = connectedSections.getRight();
        Section joinedSection = first.mergeWith(second);

        sectionDao.delete(first);
        sectionDao.delete(second);
        sectionDao.save(joinedSection);
        return ResponseEntity.ok().build();
    }
}
