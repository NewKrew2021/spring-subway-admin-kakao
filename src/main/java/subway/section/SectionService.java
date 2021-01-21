package subway.section;

import org.springframework.stereotype.Service;
import subway.line.LineRequest;
import subway.station.StationResponse;
import subway.station.StationService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SectionService {
    private static final int INITIAL_DISTANCE = 0;

    private final SectionDao sectionDao;
    private final StationService stationService;

    public SectionService(SectionDao sectionDao, StationService stationService) {
        this.sectionDao = sectionDao;
        this.stationService = stationService;
    }

    public void createSection(Long lineId, SectionRequest sectionRequest) {
        Sections sections = new Sections(sectionDao.findByLineId(lineId));
        Long upStationId = sectionRequest.getUpStationId();
        Long downStationId = sectionRequest.getDownStationId();
        int distance = sectionRequest.getDistance();

        sections.validateSection(upStationId, downStationId, distance);
        Section section = new Section(lineId,
                sections.getExtendedStationId(upStationId, downStationId),
                sections.calculateRelativeDistance(upStationId, downStationId, distance));
        sectionDao.save(section);
    }

    public void deleteSection(Long lineId, Long stationId) {
        Sections sections = new Sections(sectionDao.findByLineId(lineId));
        sections.validateDeleteStation(stationId);
        sectionDao.deleteByLineIdAndStationId(new Section(lineId, stationId, INITIAL_DISTANCE));
    }

    public void lineInitialize(Section firstSection, Section secondSection) {
        sectionDao.save(firstSection);
        sectionDao.save(secondSection);
    }

    public List<StationResponse> findSortedStationsByLineId(Long lineId) {
        Sections sections = new Sections(sectionDao.findByLineId(lineId));
        return sections.getSortedStationIdsByDistance()
                .stream()
                .map(stationService::getStationResponseById)
                .collect(Collectors.toList());
    }

}
