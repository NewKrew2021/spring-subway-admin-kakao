package subway.line;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.station.Station;
import subway.station.StationDao;
import subway.station.StationResponse;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class LineController {

    private LineDao lineDao;
    private StationDao stationDao;
    private SectionDao sectionDao;

    public LineController(LineDao lineDao, StationDao stationDao, SectionDao sectionDao){
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    @PostMapping(value = "/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        if (lineDao.findByName(lineRequest.getName()) != 0){
            return ResponseEntity.badRequest().build();
        }

        Line newLine = lineDao.save(new Line(lineRequest.getName(), lineRequest.getColor()));

        sectionDao.save(
                new Section(newLine.getId(), 0L, lineRequest.getUpStationId(), 0));
        sectionDao.save(
                new Section(newLine.getId(), lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance()));
        sectionDao.save(
                new Section(newLine.getId(), lineRequest.getDownStationId(), -1L, 0));

        return ResponseEntity.created(URI.create("/lines/" + newLine.getId())).body(
                new LineResponse(newLine, findStationsByLineId(newLine.getId()))
        );
    }

    @GetMapping("/lines")
    public ResponseEntity<List<LineResponse>> showStationsOfLine(){

        return ResponseEntity.ok().body(lineDao.findAll()
                .stream()
                .map(line -> new LineResponse(line, findStationsByLineId(line.getId())))
                .collect(Collectors.toList()));
    }

    @PutMapping("lines/{id}")
    public ResponseEntity modifyLine(@PathVariable Long id, @RequestBody LineRequest lineRequest){
        lineDao.modify(id, lineRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity deleteLineById(@PathVariable Long id) {
        lineDao.deleteById(id);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/lines/{id}/sections")
    public ResponseEntity addSection(@PathVariable Long id, @RequestBody SectionRequest sectionRequest) {
        Line line = lineDao.findById(id);
        List<Section> sections = sectionDao.findSectionsByLineId(line.getId());

        Section head = findHeadSection(sections);
        Section tail = findTailSection(sections);

        // 상행 종점 등록
        if(head.getDownStationId() == sectionRequest.getDownStationId()) {
            sectionDao.save(new Section(id, Line.HEAD, sectionRequest.getUpStationId(), 0));
            sectionDao.save(new Section(id, sectionRequest));
            sectionDao.deleteById(head.getId());
            return ResponseEntity.ok().build();
        }

       // 하행 종점 등록
        if(tail.getUpStationId() == sectionRequest.getUpStationId()) {
            sectionDao.save(new Section(id, sectionRequest.getDownStationId(), Line.TAIL, 0));
            sectionDao.save(new Section(id, sectionRequest));
            sectionDao.deleteById(tail.getId());
            return ResponseEntity.ok().build();
        }

        // 갈래길
        if(sections.contains(new Section(id, sectionRequest))){
            return ResponseEntity.status(500).build();
        }

        for (Section section : sections) {

            if(section.getUpStationId() == sectionRequest.getUpStationId()) {
                if(section.getDistance() <= sectionRequest.getDistance()){
                    return ResponseEntity.status(500).build();
                }

                sectionDao.save(new Section(id, sectionRequest.getDownStationId(), section.getDownStationId(),
                        section.getDistance()- sectionRequest.getDistance()));
                sectionDao.save(new Section(id, sectionRequest));
                sectionDao.deleteById(section.getId());


                return ResponseEntity.ok().build();
            }

            if(section.getDownStationId() == sectionRequest.getDownStationId()){
                if(section.getDistance() <= sectionRequest.getDistance()){
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

    @GetMapping("/lines/{id}")
    public ResponseEntity<LineResponse> getLineStations(@PathVariable Long id) {
        Line line = lineDao.findById(id);

        return ResponseEntity.ok().body(new LineResponse(line, findStationsByLineId(id)));
    }

    private List<StationResponse> findStationsByLineId(Long id) {
        Line line = lineDao.findById(id);

        List<Section> sections = sectionDao.findSectionsByLineId(line.getId());

        Section currentSection = findHeadSection(sections);

        List<Station> stations = new ArrayList<>();
        while (currentSection.getDownStationId() != Line.TAIL) {
            stations.add(stationDao.findById(currentSection.getDownStationId()));
            currentSection = findNextSection(sections, currentSection);
        }

        return stations.stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
    }


    @DeleteMapping("/lines/{id}/sections")
    public ResponseEntity deleteSectionByStationId(@PathVariable Long id, @RequestParam Long stationId) {
        List<Section> sections = sectionDao.findSectionsByLineId(id);

        if(sections.size() <= 3) {
            return ResponseEntity.status(500).build();
        }

        List<Section> delSections = sectionDao.findSectionsForDelete(stationId);
        Section front = null;
        Section rear = null;
        for (Section delSection : delSections) {
            if (delSection.getDownStationId() == stationId) front = delSection;
            else if (delSection.getUpStationId() == stationId) rear = delSection;
            sectionDao.deleteById(delSection.getId());
        }

        int distance = Math.min(front.getDistance(), rear.getDistance()) == 0
                ? 0 : front.getDistance() + rear.getDistance();

        sectionDao.save(new Section(id,
                front.getUpStationId(),
                rear.getDownStationId(),
                distance));

        return ResponseEntity.ok().build();
    }

    private Section findHeadSection(List<Section> sections) {
        return sections.stream()
                .filter(s -> s.getUpStationId() == Line.HEAD)
                .findAny()
                .get();
    }

    private Section findTailSection(List<Section> sections) {
        return sections.stream()
                .filter(s -> s.getDownStationId() == Line.TAIL)
                .findAny()
                .get();
    }

    private Section findNextSection(List<Section> sections, Section currentSection) {
        return sections.stream()
                .filter(section -> section.getUpStationId() == currentSection.getDownStationId())
                .findAny()
                .get();
    }

}
