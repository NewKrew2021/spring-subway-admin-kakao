package subway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import subway.dto.Line;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dto.Section;
import subway.dto.Station;
import subway.dao.StationDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SectionService {
    private static final int MIN_SECTION_SIZE = 1;
    private final StationDao stationDao;
    private final SectionDao sectionDao;
    private final LineDao lineDao;

    @Autowired
    public SectionService(StationDao stationDao,SectionDao sectionDao,LineDao lineDao){
        this.stationDao=stationDao;
        this.sectionDao=sectionDao;
        this.lineDao=lineDao;
    }

    public void insertFirstSection(Section section){
        sectionDao.save(section);
    }

    public boolean insertSection(Line nowLine, Section newSection){

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
            if(canInsertMatchingUpStation(oldSection,newSection)){
                Section modifiedSection = new Section(oldSection.getId(),oldSection.getLineId(),newSection.getDownStationId(), oldSection.getDownStationId(), oldSection.getDistance() - newSection.getDistance());
                sectionDao.modifySection(modifiedSection);
                sectionDao.save(newSection);
                return true;
            }
            if(canInsertMatchingDownStation(oldSection,newSection)){
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
        for(Section section: sectionListFromNowLine) {
            if (section.getDownStationId().equals(stationId)) {
                Section removeTargetSection = sectionListFromNowLine.get(index + 1);
                sectionDao.deleteSection(removeTargetSection.getId());
                Section modifiedSection = new Section(section.getId(), section.getLineId(), section.getUpStationId(), removeTargetSection.getDownStationId(), section.getDistance() + removeTargetSection.getDistance());
                sectionDao.modifySection(modifiedSection);
                return true;
            }
            index += 1;
        }
        return false;
    }

    public List<Station> getStationsByLine(Line line){
        List<Section> sections=sectionDao.findSectionsByLineId(line.getId());
        List<Station> result = new ArrayList<>();
        Map<Long, Long> sectionMap = new HashMap<>();
        for(Section section: sections){
            sectionMap.put(section.getUpStationId(), section.getDownStationId());
        }
        result.add(stationDao.findById(line.getUpStationId()));
        Long nextId=line.getUpStationId();
        while(sectionMap.get(nextId) != null){
            result.add(stationDao.findById(sectionMap.get(nextId)));
            nextId = sectionMap.get(nextId);
        }
        return result;
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
