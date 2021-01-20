package subway.section;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.exception.StationNotFoundException;
import subway.station.StationDao;
import subway.station.StationResponse;

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

    public void createSection(long lineId, SectionRequest request) {
        if (!sectionDao.existBy(lineId)) {
            saveInitialSections(lineId, request);
            return;
        }

        sectionDao.save(
                getSectionsBy(lineId)
                        .createNewSection(request.getUpStationId(), request.getDownStationId(), request.getDistance())
        );
    }

    private void saveInitialSections(long lineId, SectionRequest request) {
        for (Section section : Sections.initialize(lineId, request).getSections()) {
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
