package subway.line;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class LineController {

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest){
        LineDao lineDao = new LineDao();
        if(lineDao.isContainSameName(lineRequest.getName())){
            return ResponseEntity.badRequest().build();
        }
        Line newLine= lineDao.save(new Line(lineRequest));
        return ResponseEntity.created(URI.create("/line/" +newLine.getId())).body(new LineResponse(newLine.getId(),newLine.getName(),newLine.getColor(),newLine.getStations()));
    }

    @GetMapping("/lines")
    public ResponseEntity<List<LineResponse>> getLines(){
        LineDao lineDao=new LineDao();
        List<LineResponse> response = lineDao.findAll()
                .stream()
                .map(i -> new LineResponse(i))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/lines/{lineId}")
    public ResponseEntity<LineResponse> getLine(@PathVariable Long lineId){
        LineDao lineDao=new LineDao();
        Line searchedLine=lineDao.findById(lineId);
        return ResponseEntity.ok().body(new LineResponse(searchedLine.getId(),searchedLine.getName(),searchedLine.getColor(),searchedLine.getStations()));
    }

    @PutMapping("/lines/{lineId}")
    public ResponseEntity<LineResponse> updateLine(@PathVariable Long lineId, @RequestBody LineRequest lineRequest){
        LineDao lineDao=new LineDao();
        lineDao.modify(lineId, lineRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{lineId}")
    public ResponseEntity<LineResponse> deleteLine(@PathVariable Long lineId){
        LineDao lineDao =new LineDao();
        lineDao.delete(lineId);
        return  ResponseEntity.noContent().build();
    }


}
