package subway.section;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.line.LineRequest;
import subway.station.Station;
import subway.station.StationResponse;
import subway.station.StationService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SectionService {
    public static final int INITIAL_DISTANCE = 0;

    private final SectionDao sectionDao;
    private final StationService stationService;

    public SectionService(SectionDao sectionDao, StationService stationService) {
        this.sectionDao = sectionDao;
        this.stationService = stationService;
    }

    public void createSection(Long lineId, SectionRequest sectionRequest) {
        Sections sectionsByLineId = new Sections(sectionDao.findByLineId(lineId));
        Long upStationId = sectionRequest.getUpStationId();
        Long downStationId = sectionRequest.getDownStationId();
        int distance = sectionRequest.getDistance();

        sectionsByLineId.validateSection(upStationId,downStationId,distance);
        sectionDao.save(lineId,
                sectionsByLineId.getExtendedStationId(upStationId,downStationId),
                sectionsByLineId.calculateRelativeDistance(upStationId,downStationId,distance)
        );
    }

    public void deleteSection(Long lineId, Long stationId) {
        Sections sectionsByLineId = new Sections(sectionDao.findByLineId(lineId));
        sectionsByLineId.validateDeleteStation(stationId);
        sectionDao.deleteByLineIdAndStationId(lineId, stationId);
    }

    public void lineInitialize(Long lineId, LineRequest lineRequest) {
        Long upStationId = lineRequest.getUpStationId();
        Long downStationId = lineRequest.getDownStationId();
        int distance = lineRequest.getDistance();
        sectionDao.save(lineId, upStationId, INITIAL_DISTANCE);
        sectionDao.save(lineId, downStationId, distance);
    }

    public List<StationResponse> findSortedStationsByLineId(Long lineId) {
        Sections sectionsByLineId = new Sections(sectionDao.findByLineId(lineId));
        return sectionsByLineId.getSortedStationIdsByDistance()
                .stream()
                .map(stationService::getStationResponseById)
                .collect(Collectors.toList());
    }

}
