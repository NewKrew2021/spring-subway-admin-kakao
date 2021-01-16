package subway.section;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import subway.line.Line;
import subway.line.LineDao;
import subway.section.Section;
import subway.section.SectionDao;
import subway.station.Station;
import subway.station.StationDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SectionService {

    @Autowired
    private StationDao stationDao;
    @Autowired
    private SectionDao sectionDao;
    @Autowired
    private LineDao lineDao;

    private static final int MIN_SECTION_SIZE = 1;


    public boolean insertSection(Line nowLine, Section newSection){
        /*
            1.라인 왼쪽 끝에 붙는 경우 ->무조건 가능
            2.라인 오른쪽 끝에 붙는 경우 -> 무조건 가능
            3.왼쪽 + 가운데, 가운데 + 오른쪽으로 갈리는경우 ->거리가 더 짧아야함
            1,2,3 이외에는 무조건 불가능
         */
        /*
            half -> 거리 짧으면 true
                 -> 거리
         */
        // 1. 라인 왼쪽 or 오른쪽 끝에 붙는 경우




        List<Section> sectionListFromNowLine = sectionDao.findSectionsByLineId(nowLine.getId());

        if(isMatchedOnlyUpEndStation(nowLine,newSection)){
            sectionDao.save(newSection);
            nowLine.setUpStationId(newSection.getUpStationId());
            lineDao.modifyLineStationId(nowLine);
            return true;
        }

        if(isMatchedOnlyDownEndStation(nowLine,newSection)){
            sectionDao.save(newSection);
            nowLine.setDownStationId(newSection.getDownStationId());
            lineDao.modifyLineStationId(nowLine);
            return true;
        }
        for(Section oldSection: sectionListFromNowLine){
            System.out.println("==================");
            System.out.println(newSection.toString());
            System.out.println(oldSection.toString());
            System.out.println("==================");
            if(canInsertMatchingUpStation(oldSection,newSection)){
                System.out.println("매칭업");
                Section modifiedSection = new Section(oldSection.getId(),oldSection.getLineId(),newSection.getDownStationId(), oldSection.getDownStationId(), oldSection.getDistance() - newSection.getDistance());
                sectionDao.modifySection(modifiedSection);
                sectionDao.save(newSection);
                return true;
            }
            if(canInsertMatchingDownStation(oldSection,newSection)){
                System.out.println("매칭다운");
                Section modifiedSection = new Section(oldSection.getId(),oldSection.getLineId(),oldSection.getUpStationId(), newSection.getUpStationId(), oldSection.getDistance()-newSection.getDistance());
                sectionDao.modifySection(modifiedSection);
                sectionDao.save(newSection);
                return true;
            }
        }
        return false;
    }


    public boolean deleteStation(Line line, Long stationId){

        List<Section> sectionListFromNowLine = sectionDao.findSectionsByLineId(line.getId());
        for (Section section : sectionListFromNowLine) {
            System.out.println(section.toString());
        }

        if(sectionListFromNowLine.size() <= MIN_SECTION_SIZE){
            return false;
        }


        if(line.getUpStationId().equals(stationId) ){
            Section removeTargetSection  = sectionListFromNowLine.get(0);
            sectionDao.deleteSection(removeTargetSection.getId());
            lineDao.modifyLineUpStationId(line.getId(), removeTargetSection.getDownStationId());
            return true;
        }

        if(line.getDownStationId().equals(stationId)){
            Section removeTargetSection  = sectionListFromNowLine.get(sectionListFromNowLine.size() - 1);
            sectionDao.deleteSection(removeTargetSection.getId());
            lineDao.modifyLineDownStationId(line.getId(),removeTargetSection.getUpStationId());
            return true;
        }

        int index=0;
        for(Section section: sectionListFromNowLine){
            if(section.getDownStationId().equals(stationId)){
                //다운이 일치하는역을 찾으면 그 뒤역을 삭제하고
                Section removeTargetSection  = sectionListFromNowLine.get(index + 1);
                sectionDao.deleteSection(removeTargetSection.getId());
                System.out.println("삭제------------------");
                System.out.println("원본"+section.toString());
                System.out.println("지워짐:"+removeTargetSection.toString());
                Section modifiedSection=new Section(section.getId(),section.getLineId(),section.getUpStationId(),removeTargetSection.getDownStationId(),section.getDistance()+ removeTargetSection.getDistance());
                System.out.println("변경됨:"+modifiedSection.toString());
                sectionDao.modifySection(modifiedSection);
                return true;
            }
            index+=1;
        }



        return false;
    }

    public List<Station> getStationListBySectionList(List<Section> sectionList, Long endUpStationId){
        List<Station> result = new ArrayList<>();
        Map<Long, Long> sectionMap = new HashMap<>();
        for(Section section: sectionList){
            sectionMap.put(section.getUpStationId(), section.getDownStationId());
        }
        result.add(stationDao.findById(endUpStationId));
        Long nextId=endUpStationId;
        while(sectionMap.get(nextId) != null){
            result.add(stationDao.findById(sectionMap.get(nextId)));
            nextId = sectionMap.get(nextId);
        }
        return result;
    }

    public List<Section> getSectionListByLineId(Long lineId){
        return sectionDao.findSectionsByLineId(lineId);
    }

    private boolean isMatchedOnlyUpEndStation(Line nowLine, Section newSection){
       return nowLine.getUpStationId().equals(newSection.getDownStationId());
    }

    private boolean isMatchedOnlyDownEndStation(Line nowLine, Section newSection){
        return nowLine.getDownStationId().equals(newSection.getUpStationId());
    }

    private boolean canInsertMatchingUpStation(Section oldSection, Section newSection){
        return oldSection.getUpStationId().equals(newSection.getUpStationId()) &&
                !oldSection.getDownStationId().equals(newSection.getDownStationId()) &&
                oldSection.getDistance() > newSection.getDistance();
    }

    private boolean canInsertMatchingDownStation(Section oldSection,Section newSection){
        if(oldSection.getDownStationId().equals(newSection.getDownStationId())
                &&!oldSection.getUpStationId().equals(newSection.getUpStationId())
                    &&oldSection.getDistance()>newSection.getDistance()){
                return true;
        }

        return false;
    }


}
