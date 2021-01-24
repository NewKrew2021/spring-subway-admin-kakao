package subway.section;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import subway.section.domain.Section;
import subway.section.domain.Sections;
import subway.section.vo.SectionCreateValue;
import subway.section.vo.SectionResultValues;
import subway.station.StationDao;
import subway.station.domain.Station;
import subway.station.vo.StationResultValues;

import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class SectionService {
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SectionService(SectionDao sectionDao, StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    // TODO: 날씬해질 수 있을..까?
    public SectionResultValues create(SectionCreateValue createValue) {
        if (upAndDownStationsAreEqual(createValue)) {
            throw new IllegalArgumentException("UpSection and DownSection cannot be equal");
        }

        Sections sections = sectionDao.findAllSectionsOf(createValue.getLineID());
        Section upSection = new Section(createValue.getLineID(), createValue.getUpStationID(), 0);
        Section downSection = new Section(createValue.getLineID(), createValue.getDownStationID(),
                createValue.getDistanceDiff());

        if (sections.hasNoSections()) {
            sectionDao.insert(upSection);
            sectionDao.insert(downSection);

            Sections newSections = sectionDao.findAllSectionsOf(createValue.getLineID());
            return newSections.toResultValues();
        }

        Section newSection = sections.createSection(upSection, downSection);
        if (newSection == null) {
            throw new IllegalArgumentException("Cannot create section. Distance exceeds existing section distance");
        }

        sectionDao.insert(newSection);
        Sections newSections = sectionDao.findAllSectionsOf(createValue.getLineID());
        return newSections.toResultValues();
    }

    public void delete(long lineID, long stationID) {
        Sections sections = sectionDao.findAllSectionsOf(lineID);
        Section section;

        try {
            section = sectionDao.findOneByLineAndSectionIDs(lineID, stationID);
        } catch (DataAccessException e) {
            throw new NoSuchElementException(
                    String.format("%s\nCould not find section with line id: %d and section id: %d",
                            e.getMessage(), lineID, stationID));
        }

        sections.checkIsDeletable(section);
        sectionDao.delete(section);
    }

    public StationResultValues findStationsByLineID(long lineID) {
        Sections sections = sectionDao.findAllSectionsOf(lineID);
        return new StationResultValues(sections.getStationIDs()
                .stream()
                .map(Station::new)
                .map(stationDao::findByID)
                .map(Station::toResultValue)
                .collect(Collectors.toList()));
    }

    private boolean upAndDownStationsAreEqual(SectionCreateValue createValue) {
        return createValue.getDownStationID() == createValue.getUpStationID();
    }
}
