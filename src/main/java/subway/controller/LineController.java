package subway.controller;

import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.domain.Line;
import subway.domain.OrderedSections;
import subway.domain.Section;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.domain.Station;
import subway.request.LineRequest;
import subway.request.SectionRequest;
import subway.response.LineResponse;
import subway.response.SectionResponse;
import subway.dao.StationDao;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@FunctionalInterface
interface SectionToLongFunction {
    Long applyAsLong(Section section);
}

@RestController
@RequestMapping("/lines")
public class LineController {
    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    @Autowired
    public LineController(LineDao lineDao, StationDao stationDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        try {
            Line newLine = lineDao.save(lineRequest.getDomain());
            Section section = new Section(newLine.getId(), lineRequest.getUpStationId(),
                    lineRequest.getDownStationId(), lineRequest.getDistance());
            sectionDao.save(section);
            LineResponse lineResponse = new LineResponse(newLine, getOrderedStationsOfLine(newLine.getId()));
            return ResponseEntity.created(URI.create("/lines/" + newLine.getId())).body(lineResponse);
        } catch (DataAccessException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<LineResponse> response = lineDao.findAll().stream()
                .map(line -> new LineResponse(line, getOrderedStationsOfLine(line.getId())))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(response);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        Line line = lineDao.getById(id);
        List<Station> stations = getOrderedStationsOfLine(id);

        if (line != null) {
            return ResponseEntity.ok().body(new LineResponse(line, stations));
        }
        return ResponseEntity.badRequest().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity modifyLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        try {
            lineDao.update(id, new Line(lineRequest.getName(), lineRequest.getColor()));
        } catch (DataAccessException e) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        boolean response = lineDao.deleteById(id);
        if (response) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.badRequest().build();
    }

    // TODO ExceptionHandler 작성
    @PostMapping(value = "/{id}/sections", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SectionResponse> createSection(@PathVariable Long id, @RequestBody SectionRequest sectionRequest) {
        // 라인 스테이션 리스트 받기
        OrderedSections orderedSections = new OrderedSections(sectionDao.getByLineId(id));
        List<Long> stationsIds = orderedSections.getOrderedStationIds();

        // 색션 생성 가능여부 확인
        validateRequest(stationsIds, sectionRequest);

        // 추가 하행이 기존 상행 + 라스트에 붙는 케이스
        Section section = sectionRequest.getDomain();
        if (orderedSections.isAddToEdgeCase(section)) {
            Section newSection = sectionDao.save(section);
            SectionResponse sectionResponse = new SectionResponse(newSection);
            return ResponseEntity.created(URI.create("/lines/" + id + "/sections/" + newSection.getId())).body(sectionResponse);
        }

        // 쪼개지는 케이스
        Section sectionToSplit = orderedSections.findSectionToSplit(sectionRequest.getDomain());

        // 거리체크
        if (sectionToSplit.getDistance() < sectionRequest.getDistance()) {
            throw new IllegalArgumentException();
        }

        // 삭제하고 쪼개서 저장하기 TODO transaction
        Section section1 = new Section(id, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());
        Section section2 = new Section(id, sectionRequest.getDownStationId(), sectionToSplit.getDownStationId(),
                sectionToSplit.getDistance() - sectionRequest.getDistance());

        // 하행 기준 분리 케이스
        if (sectionToSplit.getDownStationId().equals(sectionRequest.getDownStationId())) {
            section2 = new Section(id, sectionToSplit.getUpStationId(), sectionRequest.getUpStationId(),
                    sectionToSplit.getDistance() - sectionRequest.getDistance());
        }

        Section newSection = sectionDao.save(section1);
        sectionDao.save(section2);
        sectionDao.deleteById(sectionToSplit.getId());

        SectionResponse sectionResponse = new SectionResponse(newSection);
        return ResponseEntity.created(URI.create("/lines/" + id + "/sections/" + newSection.getId())).body(sectionResponse);
    }

    @DeleteMapping("/{lineId}/sections")
    public ResponseEntity deleteSection(@PathVariable Long lineId, @RequestParam Long stationId) {
        List<Station> stations = getOrderedStationsOfLine(lineId);
        validate(stationId, stations);

        List<Section> sections = sectionDao.getByLineId(lineId);
        Section downSideSectionToDelete = sectionToStationId(sections, Section::getUpStationId, stationId);
        Section upsideSectionToDelete = sectionToStationId(sections, Section::getDownStationId, stationId);

        if (downSideSectionToDelete == null || upsideSectionToDelete == null) {
            Section deleteSection = downSideSectionToDelete == null ? upsideSectionToDelete : downSideSectionToDelete;
            sectionDao.deleteById(deleteSection.getId());
            return ResponseEntity.noContent().build();
        }

        Section newSection = new Section(lineId, upsideSectionToDelete.getUpStationId(), downSideSectionToDelete.getDownStationId(), downSideSectionToDelete.getDistance() + upsideSectionToDelete.getDistance());
        sectionDao.deleteById(downSideSectionToDelete.getId());
        sectionDao.deleteById(upsideSectionToDelete.getId());
        sectionDao.save(newSection);
        return ResponseEntity.noContent().build();
    }

    private Section sectionToStationId(List<Section> sections, SectionToLongFunction func, Long stationId) {
        return sections.stream()
                .filter(section -> func.applyAsLong(section).equals(stationId))
                .findFirst()
                .orElse(null);
    }

    private void validate(Long stationId, List<Station> stations) {
        if (stations.stream().noneMatch(station -> station.getId().equals(stationId)) || stations.size() <= 2) {
            throw new IllegalArgumentException();
        }
    }

    private void validateRequest(List<Long> stationIds, SectionRequest sectionRequest) {
        if (sectionRequest.getUpStationId().equals(sectionRequest.getDownStationId())) {
            throw new IllegalArgumentException();
        }

        int containedNumber = (int) stationIds.stream()
                .filter(stationId -> stationId.equals(sectionRequest.getDownStationId()) ||
                        stationId.equals(sectionRequest.getUpStationId()))
                .count();

        if (containedNumber != 1) {
            throw new IllegalArgumentException();
        }
    }

    private List<Station> getOrderedStationsOfLine(Long lineId) {
        OrderedSections orderedSections = new OrderedSections(sectionDao.getByLineId(lineId));
        List<Long> orderedStationIds = orderedSections.getOrderedStationIds();
        List<Station> stations = stationDao.batchGetByIds(orderedStationIds);
        Map<Long, String> stationNameMap = new HashMap<>();
        stations.forEach(station -> stationNameMap.put(station.getId(), station.getName()));

        return orderedStationIds.stream()
                .map(id -> new Station(id, stationNameMap.get(id)))
                .collect(Collectors.toList());
    }
}
