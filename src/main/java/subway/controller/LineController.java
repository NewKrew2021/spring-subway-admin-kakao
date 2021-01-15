package subway.controller;

import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.domain.Line;
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
            LineResponse lineResponse = new LineResponse(newLine, getLineStations(newLine.getId()));
            return ResponseEntity.created(URI.create("/lines/" + newLine.getId())).body(lineResponse);
        } catch (DataAccessException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<LineResponse> response = lineDao.findAll().stream()
                .map(line -> new LineResponse(line, getLineStations(line.getId())))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(response);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        Line line = lineDao.getById(id);
        List<Station> stations = getLineStations(id);

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
        List<Section> sections = sectionDao.getByLineId(id);
        List<Station> stations = sectionsToStations(sections);
        List<Section> sortedSections = sortByOrder(sections);

        // 색션 생성 가능여부 확인
        validateRequest(stations, sectionRequest);

        // 추가 하행이 기존 상행 + 라스트에 붙는 케이스
        if (checkWithoutSplit(sectionRequest, sortedSections)) {
            Section newSection = sectionRequest.getDomain();
            sectionDao.save(newSection);
            SectionResponse sectionResponse = new SectionResponse(newSection);
            return ResponseEntity.created(URI.create("/lines/" + id + "/sections/" + newSection.getId())).body(sectionResponse);
        }

        // 쪼개지는 케이스
        Section sectionToSplit = findSectionToSplit(sortedSections, sectionRequest);

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
        List<Station> stations = getLineStations(lineId);
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

    private boolean checkWithoutSplit(SectionRequest sectionRequest, List<Section> sortedSections) {
        return sortedSections.get(0).getUpStationId().equals(sectionRequest.getDownStationId()) ||
                sortedSections.get(sortedSections.size() - 1).getDownStationId().equals(sectionRequest.getUpStationId());
    }

    private Section findSectionToSplit(List<Section> sortedSections, SectionRequest sectionRequest) {
        Optional<Section> optionalSection = sortedSections.stream()
                .filter(section -> section.getUpStationId().equals(sectionRequest.getUpStationId()))
                .findFirst();

        return optionalSection.orElseGet(() -> sortedSections.stream()
                .filter(section -> section.getDownStationId().equals(sectionRequest.getDownStationId()))
                .findFirst().orElseThrow(IllegalArgumentException::new));

    }

    private List<Section> sortByOrder(List<Section> sections) {
        Long upStation = findFirstStation(sections);

        Map<Long, Section> connection = generateConnection(sections);

        Long currentStation = upStation;
        List<Section> orderedSections = new ArrayList<>();

        for (int i = 0; i < sections.size(); ++i) {
            Section currentSection = connection.get(currentStation);
            orderedSections.add(currentSection);
            currentStation = currentSection.getDownStationId();
        }

        return orderedSections;
    }

    private Long findFirstStation(List<Section> sections) {
        List<Long> upStations = sections.stream().map(Section::getUpStationId).collect(Collectors.toList());
        List<Long> downStations = sections.stream().map(Section::getDownStationId).collect(Collectors.toList());
        return upStations.stream().filter(station -> !downStations.contains(station)).findFirst().orElseThrow(IllegalArgumentException::new);
    }

    private Map<Long, Section> generateConnection(List<Section> sections) {
        Map<Long, Section> connection = new HashMap<>();
        for (Section section : sections) {
            connection.put(section.getUpStationId(), section);
        }
        return connection;
    }


    private void validateRequest(List<Station> stations, SectionRequest sectionRequest) {
        if (sectionRequest.getUpStationId().equals(sectionRequest.getDownStationId())) {
            throw new IllegalArgumentException();
        }

        int containedNumber = (int) stations.stream().filter(station ->
                station.getId().equals(sectionRequest.getDownStationId()) || station.getId().equals(sectionRequest.getUpStationId())).count();

        if (containedNumber != 1) {
            throw new IllegalArgumentException();
        }
    }

    private List<Station> sectionsToStations(List<Section> sections) {
        sections = sortByOrder(sections);
        List<Station> stations = sections.stream()
                .map(Section::getUpStationId)
                .map(stationDao::getById)
                .collect(Collectors.toList());
        Station lastStation = stationDao.getById(sections.get(sections.size() - 1).getDownStationId());
        stations.add(lastStation);
        return stations;
    }

    private List<Station> getLineStations(Long lineId) {
        List<Section> sections = sectionDao.getByLineId(lineId);
        return sectionsToStations(sections);
    }
}
