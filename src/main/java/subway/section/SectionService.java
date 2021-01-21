package subway.section;

import org.springframework.stereotype.Service;
import subway.section.domain.Section;
import subway.section.domain.Sections;
import subway.section.vo.SectionCreateValue;
import subway.section.vo.SectionDeleteValue;
import subway.section.vo.SectionReadStationsValue;
import subway.section.vo.SectionResultValues;
import subway.station.StationDao;
import subway.station.domain.Station;
import subway.station.vo.StationResultValues;

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

    public void delete(SectionDeleteValue deleteValue) {
        Sections sections = sectionDao.findAllSectionsOf(deleteValue.getLineID());
        if (sections.hasMinimumSectionCount()) {
            throw new IllegalArgumentException("Cannot delete section when there are only two sections left");
        }

        Section section = sectionDao.delete(new Section(deleteValue.getLineID(), deleteValue.getStationID()));
        if (isNotDeleted(section)) {
            throw new IllegalArgumentException(
                    String.format("Could not delete section with station id: %d and line id: %d",
                            deleteValue.getStationID(), deleteValue.getLineID()));
        }
    }

    public StationResultValues findStationsByLineID(SectionReadStationsValue readStationsValue) {
        Sections sections = sectionDao.findAllSectionsOf(readStationsValue.getID());
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

    private boolean isNotDeleted(Section section) {
        return section != null;
    }
}
