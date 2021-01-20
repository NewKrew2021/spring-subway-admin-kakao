package subway.section.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.exception.StationNotFoundException;
import subway.section.domain.Section;
import subway.section.domain.SectionCreateValue;
import subway.section.domain.SectionDao;
import subway.section.domain.Sections;
import subway.station.domain.StationDao;
import subway.station.presentation.StationResponse;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class SectionService {

    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SectionService(SectionDao sectionDao, StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    @Transactional(readOnly = true)
    public List<StationResponse> getStationsOf(long lineId) {
        return getSectionsBy(lineId)
                .getStations()
                .stream()
                .map(stationId -> stationDao.findById(stationId)
                        .orElseThrow(() -> new StationNotFoundException(stationId)))
                .map(StationResponse::from)
                .collect(toList());

    }

    public void createSection(SectionCreateValue createValue) {
        if (!sectionDao.existBy(createValue.getLineId())) {
            saveInitialSections(createValue);
            return;
        }

        sectionDao.save(getSectionsBy(createValue.getLineId()).createNewSection(createValue));
    }

    private void saveInitialSections(SectionCreateValue sectionValue) {
        for (Section section : Sections.initialize(sectionValue).getSections()) {
            sectionDao.save(section);
        }
    }

    public void removeSection(long lineId, long stationId) {
        getSectionsBy(lineId)
                .findSectionToDeleteBy(stationId)
                .ifPresent(sectionDao::delete);
    }

    public void removeSectionsByLine(long lineId) {
        for (Section section : getSectionsBy(lineId).getSections()) {
            sectionDao.delete(section);
        }
    }

    public Sections getSectionsBy(long lineId) {
        return Sections.from(sectionDao.findByLineId(lineId));
    }
}
