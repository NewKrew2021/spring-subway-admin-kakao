package subway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Sections;
import subway.domain.Station;
import subway.exception.InvalidSectionInsertException;
import subway.exception.NotEnoughLengthToDeleteSectionException;
import subway.exception.StationNotFoundException;

import java.util.ArrayList;
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

    public void insertSection(Line nowLine, Section newSection) throws InvalidSectionInsertException {

        Sections sections = sectionDao.findSectionsByLineId(nowLine.getId());

        if (nowLine.isMatchedOnlyUpEndStation(newSection)) {
            sectionDao.save(newSection);
            nowLine.setUpStationId(newSection.getUpStationId());
            lineDao.update(nowLine);
            return;
        }

        if (nowLine.isMatchedOnlyDownEndStation(newSection)) {
            sectionDao.save(newSection);
            nowLine.setDownStationId(newSection.getDownStationId());
            lineDao.update(nowLine);
            return;
        }

        Section modifiedSection = sections.getModifiedSection(newSection);
        if(modifiedSection == null){
            throw new InvalidSectionInsertException();
        }
        sectionDao.modifySection(modifiedSection);
        sectionDao.save(newSection);
        return;
    }


    public void deleteStation(Line line, Long stationId) throws NotEnoughLengthToDeleteSectionException, StationNotFoundException {

        Sections sections = sectionDao.findSectionsByLineId(line.getId());

        if (!sections.isPossibleLengthToDelete()) {
            throw new NotEnoughLengthToDeleteSectionException();
        }

        Section sectionMatchingDownStation = sections.getSectionByDownStationId(stationId);
        Section sectionMatchingUpStation = sections.getSectionByUpStationId(stationId);

        if(sectionMatchingDownStation == null && sectionMatchingUpStation == null){
            throw new StationNotFoundException();
        }
        if(sectionMatchingDownStation != null && sectionMatchingUpStation != null){
            sectionDao.delete(sectionMatchingUpStation.getId());
            sectionDao.modifySection(
                    new Section(
                            sectionMatchingDownStation.getId(), sectionMatchingDownStation.getLineId(), sectionMatchingDownStation.getUpStationId(), sectionMatchingUpStation.getDownStationId(), sectionMatchingDownStation.getDistance() + sectionMatchingUpStation.getDistance()));
            stationDao.delete(stationId);
            return;
        }

        if(line.isSectionContainEndDownStation(sectionMatchingDownStation)){
            line.setDownStationId(sectionMatchingDownStation.getUpStationId());
            lineDao.update(line);
            sectionDao.delete(sectionMatchingDownStation.getId());
            return;
        }

        if(line.isSectionContainUpEndStation(sectionMatchingUpStation)){
            line.setUpStationId(sectionMatchingUpStation.getDownStationId());
            lineDao.update(line);
            sectionDao.delete(sectionMatchingUpStation.getId());
            return;
        }
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
        return sectionDao.count() == 0;
    }

}
