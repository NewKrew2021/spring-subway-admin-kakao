package subway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.*;

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
        sectionDao.save(section);
    }

    public boolean insertSection(Line nowLine, Section newSection) {
        Sections sectionsFromNowLine = sectionDao.findSectionsByLineId(nowLine.getId());
        if (nowLine.isInsertModified(newSection)) {
            sectionDao.save(newSection);
            lineDao.updateLine(nowLine);
            return true;
        }
        Section matchSection = sectionsFromNowLine.findMatchSection(newSection);
        if (matchSection != null) {
            matchSection.modifyMatchedSection(newSection);
            sectionDao.modifySection(matchSection);
            sectionDao.save(newSection);
            return true;
        }
        return false;
    }

    public boolean deleteStation(Line line, Long stationId) {
        Sections sectionsFromNowLine = sectionDao.findSectionsByLineId(line.getId());
        if (!sectionsFromNowLine.validateSectionDelete()) {
            return false;
        }
        List<Section> deleteSections = sectionsFromNowLine.findDeleteSections(stationId);
        if (deleteSections.size() == 0) {
            return false;
        }
        if (deleteSections.size() == 1) {
            sectionDao.deleteSection(deleteSections.get(0).getId());
            line.modifyLineWhenSectionDelete(deleteSections.get(0));
            lineDao.updateLine(line);
            return true;
        }
        if (deleteSections.size() == 2) {
            Section modifySection = deleteSections.get(0);
            Section deleteSection = deleteSections.get(1);
            modifySection.mergeSection(deleteSection);
            sectionDao.modifySection(modifySection);
            sectionDao.deleteSection(deleteSection.getId());
            return true;
        }
        return false;
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
