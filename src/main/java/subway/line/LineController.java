package subway.line;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.section.Section;
import subway.section.SectionDao;
import subway.section.SectionService;
import subway.station.Station;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class LineController {
    @Autowired
    SectionDao sectionDao;
    @Autowired
    LineDao lineDao;
    @Autowired
    SectionService sectionService;
    @PostMapping(value = "/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest){

        if(lineDao.isContainSameName(lineRequest.getName())){
            return ResponseEntity.badRequest().build();
        }
        lineDao.save(new Line(lineRequest));
        Line newLine=lineDao.findLineByName(lineRequest.getName());
        System.out.println("색션 생성:"+newLine.getId()+" "+newLine.getDistance());

        sectionDao.save(new Section(newLine.getId(),newLine.getUpStationId(), newLine.getDownStationId(), lineRequest.getDistance()));
        List<Section> sections = sectionService.getSectionListByLineId(newLine.getId());
        List<Station> stations= sectionService.getStationListBySectionList(sections,newLine.getUpStationId());
        return ResponseEntity.created(
                URI.create("/line/" +newLine.getId()))
                .body(new LineResponse(newLine, stations));
    }

    @GetMapping("/lines")
    public ResponseEntity<List<LineResponse>> getLines(){
        // sectionService.getStationListBySectionList(sectionService.getSectionListByLineId(newLine.getId()),newLine.getUpStationId())
        List<LineResponse> response = lineDao.findAll()
                .stream()
                .map(line ->(
                        new LineResponse(line, sectionService.getStationListBySectionList(sectionService.getSectionListByLineId(line.getId()), line.getUpStationId()))
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/lines/{lineId}")
    public ResponseEntity<LineResponse> getLine(@PathVariable Long lineId){
        Line searchedLine = lineDao.findById(lineId);
        List<Section> sections = sectionService.getSectionListByLineId(searchedLine.getId());
        List<Station> stations= sectionService.getStationListBySectionList(sections,searchedLine.getUpStationId());
        return ResponseEntity.ok().body(new LineResponse(searchedLine,stations));
    }

    @PutMapping("/lines/{lineId}")
    public ResponseEntity<LineResponse> updateLine(@PathVariable Long lineId, @RequestBody LineRequest lineRequest){
        lineDao.modify(lineId, lineRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{lineId}")
    public ResponseEntity<LineResponse> deleteLine(@PathVariable Long lineId){
        lineDao.delete(lineId);
        return  ResponseEntity.noContent().build();
    }


}
