package subway.line;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.station.StationDao;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class LineController {

    SectionDao sectionDao = SectionDao.getSectionDao();
    LineDao lineDao = LineDao.getLineDao();
    @PostMapping(value = "/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest){

        if(lineDao.isContainSameName(lineRequest.getName())){
            return ResponseEntity.badRequest().build();
        }
        Line newLine= lineDao.save(new Line(lineRequest));
        sectionDao.save(new Section(newLine.getUpStationId(), newLine.getDownStationId(), newLine.getDistance()));

        return ResponseEntity.created(
                URI.create("/line/" +newLine.getId()))
                .body(new LineResponse(newLine));
    }

    @GetMapping("/lines")
    public ResponseEntity<List<LineResponse>> getLines(){
        List<LineResponse> response = lineDao.findAll()
                .stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/lines/{lineId}")
    public ResponseEntity<LineResponse> getLine(@PathVariable Long lineId){
        Line searchedLine = lineDao.findById(lineId);
        return ResponseEntity.ok().body(new LineResponse(searchedLine));
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
