package subway.section;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import subway.line.domain.Line;
import subway.section.domain.Section;
import subway.section.domain.Sections;
import subway.section.vo.SectionCreateValue;
import subway.station.StationDao;
import subway.station.domain.Stations;

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

    public void create(SectionCreateValue createValue) {
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

            return;
        }

        Section newSection = sections.getNewSectionIfValid(createValue);
        sectionDao.insert(newSection);
    }

    public void delete(long lineID, long stationID) {
        Sections sections = sectionDao.findAllSectionsOf(lineID);
        Section section = findSectionBy(lineID, stationID);

        sections.checkIsDeletable(section);
        sectionDao.delete(section);
    }

    public Stations findStationValuesByLine(Line line) {
        Sections sections = sectionDao.findAllSectionsOf(line.getID());
        return new Stations(sections.getStationIDs()
                .stream()
                .map(stationDao::findByID)
                .collect(Collectors.toList()));
    }

    private Section findSectionBy(long lineID, long stationID) {
        try {
            return sectionDao.findOneBy(lineID, stationID);
        } catch (DataAccessException e) {
            throw new NoSuchElementException(
                    String.format("%s\nCould not find section with line id: %d and section id: %d",
                            e.getMessage(), lineID, stationID));
        }
    }

    private boolean upAndDownStationsAreEqual(SectionCreateValue createValue) {
        return createValue.getDownStationID() == createValue.getUpStationID();
    }
}
