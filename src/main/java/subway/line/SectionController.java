package subway.line;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.station.Station;
import subway.station.StationDao;

import javax.websocket.server.PathParam;
import java.net.URI;

@RestController
public class SectionController {
    SectionDao sectionDao=SectionDao.getSectionDao();
    StationDao stationDao=StationDao.getStationDao();
    LineDao lineDao = LineDao.getLineDao();
    @PostMapping("/lines/{lineId}/sections")
    public ResponseEntity<SectionResponse> createSection(@PathVariable Long lineId, @RequestBody SectionRequest sectionRequest){

        /*
            1.라인 왼쪽 끝에 붙는 경우
            2.라인 오른쪽 끝에 붙는 경우
            3.왼쪽 + 가운데, 가운데 + 오른쪽으로 갈리는경우
         */

        Line nowLine = lineDao.findById(lineId);



        SectionService sectionService = new SectionService(stationDao, sectionDao);
        Section newSection = new Section(sectionRequest);
        if(sectionService.insertSection(nowLine, newSection)){
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @DeleteMapping("/lines/{lineId}/sections")
    public ResponseEntity<SectionResponse> deleteStation(@PathVariable("lineId") Long lineId, @RequestParam("stationId") Long stationId){
        System.out.println("실행됨");
        SectionService sectionService = new SectionService(stationDao, sectionDao);
        Line nowLine = lineDao.findById(lineId);

        if(sectionService.deleteStation(nowLine,stationId)){
            System.out.println("정상");
            return ResponseEntity.ok().build();
        }
        System.out.println("실패");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }



}