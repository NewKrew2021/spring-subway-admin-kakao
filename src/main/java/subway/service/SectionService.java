package subway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.dto.Line;
import subway.dto.Section;
import subway.dto.Sections;
import subway.dto.Station;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SectionService {
    private final StationDao stationDao;
    private final SectionDao sectionDao;
    private final LineDao lineDao;

    @Autowired
    public SectionService(StationDao stationDao, SectionDao sectionDao, LineDao lineDao) {
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
        this.lineDao = lineDao;
    }

    public void insertFirstSection(Section section) {
        if(!isEmpty()) return;
        sectionDao.save(section);
    }

    public boolean insertSection(Line nowLine, Section newSection) {

        Sections sections = sectionDao.findSectionsByLineId(nowLine.getId());

        if (nowLine.isMatchedOnlyUpEndStation(newSection)) {
            sectionDao.save(newSection);
            nowLine.setUpStationId(newSection.getUpStationId());
            lineDao.modifyLineStationId(nowLine);
            return true;
        }

        if (nowLine.isMatchedOnlyDownEndStation(newSection)) {
            sectionDao.save(newSection);
            nowLine.setDownStationId(newSection.getDownStationId());
            lineDao.modifyLineStationId(nowLine);
            return true;
        }

        Section modifiedSection = sections.getModifiedSection(newSection);
        if(modifiedSection == null){
            return false;
        }
        sectionDao.modifySection(modifiedSection);
        sectionDao.save(newSection);
        return true;
    }


    public boolean deleteStation(Line line, Long stationId) {

        Sections sections = sectionDao.findSectionsByLineId(line.getId());
        System.out.println("111111111111");

        if (!sections.isPossibleLengthToDelete()) {
            return false;
        }

        Section sectionMatchingDownStation = sections.getSectionByDownStationId(stationId);
        Section sectionMatchingUpStation = sections.getSectionByUpStationId(stationId);

        if(sectionMatchingDownStation == null && sectionMatchingUpStation == null){
            return false;
        }
        if(sectionMatchingDownStation != null && sectionMatchingUpStation != null){
            sectionDao.deleteSection(sectionMatchingUpStation.getId());
            sectionDao.modifySection(
                    new Section(
                            sectionMatchingDownStation.getId(), sectionMatchingDownStation.getLineId(), sectionMatchingDownStation.getUpStationId(), sectionMatchingUpStation.getDownStationId(), sectionMatchingDownStation.getDistance() + sectionMatchingUpStation.getDistance()));
            stationDao.deleteById(stationId);
            return true;
        }

        if(line.isSectionContainEndDownStation(sectionMatchingDownStation)){
            lineDao.modifyLineDownStationId(sectionMatchingDownStation.getId(), sectionMatchingDownStation.getUpStationId());
            sectionDao.deleteSection(sectionMatchingDownStation.getId());
            return true;
        }

        if(line.isSectionContainUpEndStation(sectionMatchingUpStation)){
            lineDao.modifyLineUpStationId(sectionMatchingUpStation.getId(), sectionMatchingUpStation.getDownStationId());
            sectionDao.deleteSection(sectionMatchingUpStation.getId());
            return true;
        }
        return false;
    }

    public List<Station> getStationsByLine(Line line) {
        Sections sections = sectionDao.findSectionsByLineId(line.getId());
        List<Station> result = new ArrayList<>();
        Map<Long, Long> sectionMap = sections.getSectionMap();
        result.add(stationDao.findById(line.getUpStationId()));
        Long nextId = line.getUpStationId();
        while (sectionMap.get(nextId) != null) {
            result.add(stationDao.findById(sectionMap.get(nextId)));
            nextId = sectionMap.get(nextId);
        }
        return result;
    }

    private boolean isEmpty(){
        return sectionDao.countSection() == 0;
    }

}
