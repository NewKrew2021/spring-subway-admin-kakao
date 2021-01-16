package subway.section;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.line.*;
import subway.station.StationDao;

@RestController
public class SectionController {
    @Autowired
    SectionDao sectionDao;
    @Autowired
    StationDao stationDao;
    @Autowired
    LineDao lineDao;
    @Autowired
    SectionService sectionService;
    @PostMapping("/lines/{lineId}/sections")
    public ResponseEntity<SectionResponse> createSection(@PathVariable Long lineId, @RequestBody SectionRequest sectionRequest){

        /*
            1.라인 왼쪽 끝에 붙는 경우
            2.라인 오른쪽 끝에 붙는 경우
            3.왼쪽 + 가운데, 가운데 + 오른쪽으로 갈리는경우
         */

        Line nowLine = lineDao.findById(lineId);

        Section newSection = new Section(lineId,sectionRequest.getUpStationId(),sectionRequest.getDownStationId(),sectionRequest.getDistance());
        if(sectionService.insertSection(nowLine, newSection)){
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @DeleteMapping("/lines/{lineId}/sections")
    public ResponseEntity<SectionResponse> deleteStation(@PathVariable("lineId") Long lineId, @RequestParam("stationId") Long stationId){
        System.out.println("실행됨");
        Line nowLine = lineDao.findById(lineId);

        if(sectionService.deleteStation(nowLine,stationId)){
            System.out.println("정상");
            return ResponseEntity.ok().build();
        }
        System.out.println("실패");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }



}
