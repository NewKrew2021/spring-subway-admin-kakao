package subway.line;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.exception.InvalidIdException;
import subway.station.Station;
import subway.station.StationDao;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

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

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity illegalArgumentExceptionHandler() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @ExceptionHandler(value = {DataAccessException.class, InvalidIdException.class})
    public ResponseEntity exceptionHandler() {
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Line newLine = lineDao.save(new Line(lineRequest.getName(), lineRequest.getColor()));
        Section section = new Section(newLine.getId(), lineRequest.getUpStationId(),
                lineRequest.getDownStationId(), lineRequest.getDistance());
        sectionDao.save(section);

        LineResponse lineResponse = new LineResponse(newLine, getLineStations(newLine.getId()));
        return ResponseEntity.created(URI.create("/lines/" + newLine.getId())).body(lineResponse);
    }

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<LineResponse> response = lineDao.findAll().stream()
                .map(line -> new LineResponse(line, getLineStations(line.getId())))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        Line line = lineDao.getById(id);
        List<Station> stations = getLineStations(id);
        if (line == null) {
            throw new InvalidIdException("존재하지 않는 Line ID 입니다. Line ID : " + id);
        }
        return ResponseEntity.ok().body(new LineResponse(line, stations));
    }

    @PutMapping("/{id}")
    public ResponseEntity modifyLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        lineDao.update(id, new Line(lineRequest.getName(), lineRequest.getColor()));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        boolean response = lineDao.deleteById(id);
        if (!response) {
            throw new InvalidIdException("존재하지 않는 Line ID 입니다. Line ID : " + id);
        }
        return ResponseEntity.noContent().build();
    }

    // TODO ExceptionHandler 작성
    @PostMapping("/{id}/sections")
    public ResponseEntity<SectionResponse> createSection(@PathVariable Long id, @RequestBody SectionRequest sectionRequest) {
        // 라인 스테이션 리스트 받기
        List<Section> sections = sectionDao.getByLineId(id);
        List<Station> stations = sectionsToStations(sections);
        List<Section> sortedSections = sortByOrder(sections);

        // 색션 생성 가능여부 확인
        validateSectionRequest(stations, sectionRequest);
        // 추가 하행이 기존 상행 + 라스트에 붙는 케이스
        if (checkWithoutSplit(sectionRequest, sortedSections)) {
            SectionResponse sectionResponse = createSectionWithoutSplit(id, sectionRequest);
            return ResponseEntity.created(URI.create("/lines/" + id + "/sections/" + sectionResponse.getId())).body(sectionResponse);
        }
        // 쪼개지는 케이스
        SectionResponse sectionResponse = createSectionWithSplit(id, sortedSections, sectionRequest);
        return ResponseEntity.created(URI.create("/lines/" + id + "/sections/" + sectionResponse.getId())).body(sectionResponse);
    }

    @DeleteMapping("/{lineId}/sections")
    public ResponseEntity deleteSection(@PathVariable Long lineId, @RequestParam Long stationId) {
        List<Station> stations = getLineStations(lineId);
        validateStationInLine(stationId, stations);

        List<Section> sections = sectionDao.getByLineId(lineId);
        Section downSideSectionToDelete = getStationIdFromSection(sections, Section::getUp_station_id, stationId);
        Section upsideSectionToDelete = getStationIdFromSection(sections, Section::getDown_station_id, stationId);

        if (downSideSectionToDelete == null || upsideSectionToDelete == null) {
            Section deleteSection = downSideSectionToDelete == null ? upsideSectionToDelete : downSideSectionToDelete;
            sectionDao.deleteById(deleteSection.getId());
            return ResponseEntity.ok().build();
        }

        Section newSection = new Section(lineId,
                upsideSectionToDelete.getUp_station_id(),
                downSideSectionToDelete.getDown_station_id(),
                downSideSectionToDelete.getDistance() + upsideSectionToDelete.getDistance());

        sectionDao.deleteById(downSideSectionToDelete.getId());
        sectionDao.deleteById(upsideSectionToDelete.getId());
        return ResponseEntity.ok(sectionDao.save(newSection));
    }

    private SectionResponse createSectionWithoutSplit(Long id, SectionRequest sectionRequest) {
        Section newSection = sectionDao.save(new Section(id, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance()));
        return new SectionResponse(newSection);
    }

    private SectionResponse createSectionWithSplit(Long lineId, List<Section> sections, SectionRequest sectionRequest) {
        Section sectionToSplit = findSectionToSplit(sections, sectionRequest);
        // 거리체크
        validateDistance(sectionToSplit, sectionRequest);

        // 상행 기준 분리 케이스
        Section section1 = new Section(lineId, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());
        Section section2 = new Section(lineId, sectionRequest.getDownStationId(), sectionToSplit.getDown_station_id(),
                sectionToSplit.getDistance() - sectionRequest.getDistance());

        // 하행 기준 분리 케이스
        if (sectionToSplit.getDown_station_id().equals(sectionRequest.getDownStationId())) {
            section2 = new Section(lineId, sectionToSplit.getUp_station_id(), sectionRequest.getUpStationId(),
                    sectionToSplit.getDistance() - sectionRequest.getDistance());
        }

        Section newSection = sectionDao.save(section1);
        sectionDao.save(section2);
        sectionDao.deleteById(sectionToSplit.getId());
        return new SectionResponse(newSection);
    }

    // Todo
    private void validateDistance(Section sectionToSplit, SectionRequest sectionRequest) {
        if (sectionRequest.getDistance() <= 0
                || sectionToSplit.getDistance() < sectionRequest.getDistance()) {
            throw new IllegalArgumentException();
        }
    }

    private Section getStationIdFromSection(List<Section> sections, SectionToStationId func, Long stationId) {
        return sections.stream()
                .filter(section -> func.getStationId(section).equals(stationId))
                .findFirst()
                .orElse(null);
    }

    private void validateStationInLine(Long stationId, List<Station> stations) {
        if (stations.stream().noneMatch(station -> station.getId().equals(stationId)) || stations.size() <= 2) {
            throw new IllegalArgumentException();
        }
    }

    private boolean checkWithoutSplit(SectionRequest sectionRequest, List<Section> sortedSections) {
        return sortedSections.get(0).getUp_station_id().equals(sectionRequest.getDownStationId()) ||
                sortedSections.get(sortedSections.size() - 1).getDown_station_id().equals(sectionRequest.getUpStationId());
    }

    private Section findSectionToSplit(List<Section> sortedSections, SectionRequest sectionRequest) {
        Optional<Section> optionalSection = sortedSections.stream()
                .filter(section -> section.getUp_station_id().equals(sectionRequest.getUpStationId()))
                .findFirst();

        return optionalSection.orElseGet(() -> sortedSections.stream()
                .filter(section -> section.getDown_station_id().equals(sectionRequest.getDownStationId()))
                .findFirst().orElseThrow(IllegalArgumentException::new));

    }

    private List<Section> sortByOrder(List<Section> sections) {
        Map<Long, Section> connection = generateConnection(sections);
        Long currentStation = findFirstStation(sections);
        List<Section> orderedSections = new ArrayList<>();
        for (int i = 0; i < sections.size(); ++i) {
            Section currentSection = connection.get(currentStation);
            orderedSections.add(currentSection);
            currentStation = currentSection.getDown_station_id();
        }
        return orderedSections;
    }

    private Long findFirstStation(List<Section> sections) {
        List<Long> upStations = sections.stream().map(Section::getUp_station_id).collect(Collectors.toList());
        List<Long> downStations = sections.stream().map(Section::getDown_station_id).collect(Collectors.toList());
        return upStations.stream().filter(station -> !downStations.contains(station)).findFirst().orElseThrow(IllegalArgumentException::new);
    }

    private Map<Long, Section> generateConnection(List<Section> sections) {
        Map<Long, Section> connection = new HashMap<>();
        for (Section section : sections) {
            connection.put(section.getUp_station_id(), section);
        }
        return connection;
    }


    private void validateSectionRequest(List<Station> stations, SectionRequest sectionRequest) {
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
                .map(Section::getUp_station_id)
                .map(stationDao::getById)
                .collect(Collectors.toList());
        Station lastStation = stationDao.getById(sections.get(sections.size() - 1).getDown_station_id());
        stations.add(lastStation);
        return stations;
    }

    private List<Station> getLineStations(Long lineId) {
        List<Section> sections = sectionDao.getByLineId(lineId);
        return sectionsToStations(sections);
    }
}
