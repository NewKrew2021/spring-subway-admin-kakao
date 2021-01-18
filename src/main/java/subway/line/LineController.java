package subway.line;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.station.StationDao;
import subway.station.StationResponse;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class LineController {

    private final LineService lineService;
    private final SectionService sectionService;
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public LineController(LineService lineService, StationDao stationDao, SectionDao sectionDao, SectionService sectionService) {
        this.lineService = lineService;
        this.stationDao = stationDao;
        this.sectionService = sectionService;
        this.sectionDao = sectionDao;
    }

    @PostMapping(value = "/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        if (lineService.isLineNameExist(lineRequest.getName())) {
            return ResponseEntity.badRequest().build();
        }

        Line newLine = lineService.save(lineRequest);
        sectionService.save(newLine, lineRequest);

        return ResponseEntity.created(URI.create("/lines/" + newLine.getId()))
                .body(new LineResponse(newLine, Collections.emptyList()));
    }

    @GetMapping("/lines/{id}")
    public ResponseEntity<LineResponse> getLineStations(@PathVariable Long id) {
        Line line = lineService.findById(id);
        return ResponseEntity.ok().body(new LineResponse(line, getStationsResponseOfLineId(id)));
    }

    @PutMapping("/lines/{id}")
    public ResponseEntity modifyLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        lineService.modify(id, lineRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity deleteLineById(@PathVariable Long id) {
        lineService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/lines/{id}/sections")
    public ResponseEntity addSection(@PathVariable Long id, @RequestBody SectionRequest sectionRequest) {
        Line line = lineService.findById(id);
        Sections sections = sectionDao.findSectionsByLineId(line.getId());

        Section head = sections.findHeadSection();
        Section tail = sections.findTailSection();

        // 상행 종점 등록
        if (head.getDownStationId() == sectionRequest.getDownStationId()) {
            sectionDao.save(new Section(id, Line.HEAD, sectionRequest.getUpStationId(), 0));
            sectionDao.save(new Section(id, sectionRequest));
            sectionDao.deleteById(head.getId());
            return ResponseEntity.ok().build();
        }

        // 하행 종점 등록
        if (tail.getUpStationId() == sectionRequest.getUpStationId()) {
            sectionDao.save(new Section(id, sectionRequest.getDownStationId(), Line.TAIL, 0));
            sectionDao.save(new Section(id, sectionRequest));
            sectionDao.deleteById(tail.getId());
            return ResponseEntity.ok().build();
        }

        // 갈래길
        if (sections.contains(new Section(id, sectionRequest))) {
            return ResponseEntity.status(500).build();
        }

        for (Section section : sections.getSections()) {

            if (section.getUpStationId() == sectionRequest.getUpStationId()) {
                if (section.getDistance() <= sectionRequest.getDistance()) {
                    return ResponseEntity.status(500).build();
                }

                sectionDao.save(new Section(id, sectionRequest.getDownStationId(), section.getDownStationId(),
                        section.getDistance() - sectionRequest.getDistance()));
                sectionDao.save(new Section(id, sectionRequest));
                sectionDao.deleteById(section.getId());


                return ResponseEntity.ok().build();
            }

            if (section.getDownStationId() == sectionRequest.getDownStationId()) {
                if (section.getDistance() <= sectionRequest.getDistance()) {
                    return ResponseEntity.status(500).build();
                }

                sectionDao.save(new Section(id, section.getUpStationId(), sectionRequest.getUpStationId(),
                        section.getDistance() - sectionRequest.getDistance()));
                sectionDao.save(new Section(id, sectionRequest));
                sectionDao.deleteById(section.getId());

                return ResponseEntity.ok().build();
            }
        }

        return ResponseEntity.status(500).build();
    }

    @DeleteMapping("/lines/{id}/sections")
    public ResponseEntity deleteSectionByStationId(@PathVariable Long id, @RequestParam Long stationId) {
        if (lineService.canNotDelete(id)) {
            return ResponseEntity.status(500).build();
        }
        lineService.deleteStation(id, stationId);
        return ResponseEntity.ok().build();
    }

    private List<StationResponse> getStationsResponseOfLineId(Long lineId) {
        return lineService.findStationsOfLine(lineId).stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/lines")
    public ResponseEntity<List<LineResponse>> getAllLines() {
        return ResponseEntity.ok().body(lineService.findAll().stream()
                .map(line -> new LineResponse(line, getStationsResponseOfLineId(line.getId())))
                .collect(Collectors.toList()));
    }

}
