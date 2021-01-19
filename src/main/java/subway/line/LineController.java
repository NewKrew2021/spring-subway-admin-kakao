package subway.line;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.station.Station;
import subway.station.StationDao;
import subway.station.StationResponse;

import java.net.URI;
import java.util.stream.Collectors;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/lines")
public class LineController {

    private LineDao lineDao;
    private StationDao stationDao;
    private SectionDao sectionDao;

    public LineController(LineDao lineDao, StationDao stationDao, SectionDao sectionDao){
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        if (lineDao.findByName(lineRequest.getName()) != 0){
            return ResponseEntity.badRequest().build();
        }

        Line newLine = lineDao.save(new Line(lineRequest.getName(), lineRequest.getColor()));

        sectionDao.save(
                new Section(newLine.getId(), Line.HEAD, lineRequest.getUpStationId(), Line.INF));
        sectionDao.save(
                new Section(newLine.getId(), lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance()));
        sectionDao.save(
                new Section(newLine.getId(), lineRequest.getDownStationId(), Line.TAIL, Line.INF));

        return ResponseEntity.created(URI.create("/lines/" + newLine.getId())).body(
                new LineResponse(newLine, findStationsByLineId(newLine.getId()))
        );
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> showStationsOfLine(){

        return ResponseEntity.ok().body(lineDao.findAll()
                .stream()
                .map(line -> new LineResponse(line, findStationsByLineId(line.getId())))
                .collect(Collectors.toList()));
    }

    @PutMapping("/{id}")
    public ResponseEntity modifyLine(@PathVariable Long id, @RequestBody LineRequest lineRequest){
        lineDao.modify(id, lineRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteLineById(@PathVariable Long id) {
        lineDao.deleteById(id);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/{id}/sections")
    public ResponseEntity addSection(@PathVariable Long id, @RequestBody SectionRequest sectionRequest) {
        Line line = lineDao.findById(id);
        Sections sections = new Sections(sectionDao.findSectionsByLineId(line.getId()));
        Section newSection = new Section(id, sectionRequest);

        if(sections.hasSameSection(newSection)){
            throw new IllegalArgumentException("이미 존재하는 구간입니다.");
        }

        if(sections.isNotExistStations(newSection)){
            throw new IllegalArgumentException("요청한 역 중 하나는 노선에 존재해야 합니다.");
        }

        Section originSection = sections.sameUpStationOrDownStation(newSection);
        Section subSection = originSection.getSubSection(newSection);

        sectionDao.save(newSection);
        sectionDao.save(subSection);
        sectionDao.deleteById(originSection.getId());

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> getLineStations(@PathVariable Long id) {
        Line line = lineDao.findById(id);

        return ResponseEntity.ok().body(new LineResponse(line, findStationsByLineId(id)));
    }

    @DeleteMapping("/{id}/sections")
    public ResponseEntity deleteSectionByStationId(@PathVariable Long id, @RequestParam Long stationId) {
        int sectionsCount = sectionDao.countByLineId(id);

        if(sectionsCount <= 3) {
            throw new IllegalArgumentException("마지막 구간은 삭제할 수 없습니다");
        }

        Section front = sectionDao.findSectionByLineIdAndDownStationId(id, stationId);
        Section rear = sectionDao.findSectionByLineIdAndUpStationId(id, stationId);
        sectionDao.deleteById(front.getId());
        sectionDao.deleteById(rear.getId());

        int distance = front.getDistance() + rear.getDistance();

        sectionDao.save(new Section(id,
                front.getUpStationId(),
                rear.getDownStationId(),
                distance));

        return ResponseEntity.ok().build();
    }

    private List<StationResponse> findStationsByLineId(Long id) {
        Line line = lineDao.findById(id);

        List<Section> sections = sectionDao.findSectionsByLineId(line.getId());

        Section currentSection = sectionDao.findSectionByLineIdAndUpStationId(id, Line.HEAD);

        List<Station> stations = new ArrayList<>();
        while (currentSection.getDownStationId() != Line.TAIL) {
            stations.add(stationDao.findById(currentSection.getDownStationId()));
            currentSection = findNextSection(sections, currentSection);
        }

        return stations.stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
    }


    private Section findNextSection(List<Section> sections, Section currentSection) {
        return sections.stream()
                .filter(section -> section.getUpStationId() == currentSection.getDownStationId())
                .findAny()
                .get();
    }

}
