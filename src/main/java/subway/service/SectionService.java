package subway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.*;
import subway.exception.EmptySectionException;
import subway.exception.NoSectionSpaceException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SectionService {
    private final StationDao stationDao;
    private final SectionDao sectionDao;
    private final LineDao lineDao;

    private final int NOTHING=0;
    private final int MATCHED_ONE_STATION=1;
    private final int MATCHED_TWO_STATION=2;

    @Autowired
    public SectionService(StationDao stationDao, SectionDao sectionDao, LineDao lineDao) {
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
        this.lineDao = lineDao;
    }

    public void insertFirstSection(Section section) {
        sectionDao.save(section);
    }

    @Transactional
    public void insertSection(Line nowLine, Section newSection) throws NoSectionSpaceException {
        Sections sectionsFromNowLine = sectionDao.findSectionsByLineId(nowLine.getId());
        if (nowLine.isInsertModified(newSection)) {
            sectionDao.save(newSection);
            lineDao.updateLine(nowLine);
            return;
        }
        Section matchSection = sectionsFromNowLine.findMatchSection(newSection);
        if (matchSection == null) {
            throw new NoSectionSpaceException();
        }
        matchSection.modifyMatchedSection(newSection);
        sectionDao.modifySection(matchSection);
        sectionDao.save(newSection);
    }

    @Transactional
    public void deleteStation(Line line, Long stationId) throws EmptySectionException {
        Sections sectionsFromNowLine = sectionDao.findSectionsByLineId(line.getId());
        if (!sectionsFromNowLine.validateSectionDelete()) {
            throw new EmptySectionException();
        }
        List<Section> deleteSections = sectionsFromNowLine.findDeleteSections(stationId);
        if (deleteSections.size() == NOTHING) {
            throw new EmptySectionException();
        }
        if (deleteSections.size() == MATCHED_ONE_STATION) {
            sectionDao.deleteSection(deleteSections.get(0).getId());
            line.modifyLineWhenSectionDelete(deleteSections.get(0));
            lineDao.updateLine(line);
        }
        if (deleteSections.size() == MATCHED_TWO_STATION) {
            Section modifySection = deleteSections.get(0);
            Section deleteSection = deleteSections.get(1);
            modifySection.mergeSection(deleteSection);
            sectionDao.modifySection(modifySection);
            sectionDao.deleteSection(deleteSection.getId());
        }
    }

    public Stations getStationsByLine(Line line) {
        Sections sections = sectionDao.findSectionsByLineId(line.getId());
        List<Station> stations = new ArrayList<>();
        Map<Long, Long> sectionMap = sections.getSectionMap();
        stations.add(stationDao.findById(line.getUpStationId()));
        Long nextId = line.getUpStationId();
        while (sectionMap.get(nextId) != null) {
            stations.add(stationDao.findById(sectionMap.get(nextId)));
            nextId = sectionMap.get(nextId);
        }
        return new Stations(stations);
    }
}
