package subway.line;

import subway.station.StationDao;

import java.util.ArrayList;
import java.util.List;

public class SectionService {


    private final StationDao stationDao;
    private final SectionDao sectionDao;
    private static final int MIN_SECTION_SIZE = 1;

    public SectionService(StationDao stationDao, SectionDao sectionDao){
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }
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


        System.out.println("**********==========================");
        nowLine.getSections().stream().forEach(System.out::println);
        System.out.println(newSection.toString());
        System.out.println(nowLine.getSections());
        System.out.println("**********==========================");

        if(isMatchedOnlyUpEndStation(nowLine,newSection)){
            nowLine.getSections().add(0, newSection);
            nowLine.setUpStationId(newSection.getUpStationId());
            return true;
        }

        if(isMatchedOnlyDownEndStation(nowLine,newSection)){
            nowLine.getSections().add(newSection);
            nowLine.setDownStationId(newSection.getDownStationId());
            return true;
        }
        int index=0;
        for(Section oldSection: nowLine.getSections()){

            if(canInsertMatchingUpStation(oldSection,newSection)){
                nowLine.getSections().set(
                        index, new Section(newSection.getDownStationId(), oldSection.getDownStationId(), oldSection.getDistance() - newSection.getDistance()));
                nowLine.getSections().add(index, newSection);
                return true;
            }

            if(canInsertMatchingDownStation(oldSection,newSection)){
                nowLine.getSections().set(index,new Section(oldSection.getUpStationId(), newSection.getUpStationId(), oldSection.getDistance()-newSection.getDistance()));
                nowLine.getSections().add(index+1,newSection);
                return true;
            }
            index+=1;
        }

        return false;
    }


    public boolean deleteStation(Line line, Long stationId){
        if(line.getSections().size() <= MIN_SECTION_SIZE){
            return false;
        }


        if(line.getUpStationId().equals(stationId) ){
            Section removedLine  = line.getSections().get(0);
            line.getSections().remove(removedLine);
            line.setUpStationId(removedLine.getDownStationId());
            return true;
        }

        if(line.getDownStationId().equals(stationId)){
            Section removedLine  = line.getSections().get(-1);
            line.getSections().remove(removedLine);
            line.setDownStationId(removedLine.getUpStationId());
            return true;
        }

        int index=0;
        for(Section section: line.getSections()){
            if(section.getDownStationId().equals(stationId)){
                Section removeTargetSection  = line.getSections().get(index + 1);
                line.getSections().remove(removeTargetSection);
                section.combineSection(removeTargetSection);
                return true;
            }
            index+=1;
        }



        return false;
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
