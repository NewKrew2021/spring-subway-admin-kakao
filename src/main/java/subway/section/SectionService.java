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

    public void initializeByLine(long lineId, SectionRequest request) {
        sectionDao.save(new Section(lineId, request.getUpStationId(), 0));
        sectionDao.save(new Section(lineId, request.getDownStationId(), request.getDistance()));
    }

    @Transactional(readOnly = true)
    public List<StationResponse> findStationsOf(long lineId) {
        return findSectionsBy(lineId)
                .getStations()
                .stream()
                .map(stationId -> stationDao.findById(stationId)
                        .orElseThrow(() -> new StationNotFoundException(stationId)))
                .map(StationResponse::from)
                .collect(toList());

    }

    public void createSection(long lineId, SectionRequest request) {
        sectionDao.save(
                findSectionsBy(lineId)
                        .createNewSection(request.getUpStationId(), request.getDownStationId(), request.getDistance())
        );
    }

    public void removeSection(long lineId, long stationId) {
        findSectionsBy(lineId)
                .findSectionToDeleteBy(stationId)
                .ifPresent(sectionDao::delete);
    }

    public Sections findSectionsBy(long lineId) {
        return Sections.from(sectionDao.findByLineId(lineId));
    }
}
