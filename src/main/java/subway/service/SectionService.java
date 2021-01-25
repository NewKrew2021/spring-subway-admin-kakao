package subway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import subway.dao.*;
import subway.domain.*;
import subway.exception.InvalidSectionInsertException;
import subway.exception.NotEnoughLengthToDeleteSectionException;
import subway.exception.StationNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        if(!isEmpty()) {
            return;
        }
        sectionDao.save(section);
    }

    public void insertSection(Line nowLine, Section newSection) {

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

        Section modifiedSection = sections.getModifiedSection(newSection).orElseThrow(InvalidSectionInsertException::new);
        sectionDao.update(modifiedSection);
        sectionDao.save(newSection);
    }


    public void deleteStation(Line line, Long stationId) {

        Sections sections = sectionDao.findSectionsByLineId(line.getId());

        if (!sections.isPossibleLengthToDelete()) {
            throw new NotEnoughLengthToDeleteSectionException();
        }

        Optional<Section> sectionMatchingDownStation = sections.getSectionByDownStationId(stationId);
        Optional<Section> sectionMatchingUpStation = sections.getSectionByUpStationId(stationId);

        if(!sectionMatchingDownStation.isPresent() && !sectionMatchingUpStation.isPresent()){
            throw new StationNotFoundException();
        }
        if(sectionMatchingDownStation.isPresent() && sectionMatchingUpStation.isPresent()){
            sectionDao.delete(sectionMatchingUpStation.get().getId());
            sectionMatchingDownStation.get().modifyDownByDeletingSection(sectionMatchingUpStation.get());
            sectionDao.update(sectionMatchingDownStation.get());
            stationDao.delete(stationId);
            return;
        }

        if(line.isEqualToUpStationId(stationId)){
            line.setDownStationId(sectionMatchingDownStation.get().getUpStationId());
            lineDao.update(line);
            sectionDao.delete(sectionMatchingDownStation.get().getId());
            return;
        }

        if(line.isEqualToDownStationId(stationId)){
            line.setUpStationId(sectionMatchingUpStation.get().getDownStationId());
            lineDao.update(line);
            sectionDao.delete(sectionMatchingUpStation.get().getId());
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
