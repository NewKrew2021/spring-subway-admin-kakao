package subway.station.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.exception.DeleteSectionException;
import subway.line.service.LineService;
import subway.section.dao.SectionDao;
import subway.section.domain.Sections;
import subway.station.dao.StationDao;
import subway.station.domain.Station;
import subway.station.domain.StationRequest;
import subway.station.domain.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {

    private final StationDao stationDao;
    private final SectionDao sectionDao;
    private final LineService lineService;

    @Autowired
    public StationService(StationDao stationDao, SectionDao sectionDao, LineService lineService) {
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
        this.lineService = lineService;
    }

    @Transactional
    public StationResponse createStation(StationRequest stationRequest) {
        Long stationId = stationDao.save(new Station(stationRequest.getName()));
        Station newStation = stationDao.findById(stationId);

        return new StationResponse(stationId, newStation.getName());
    }

    public List<StationResponse> showStations() {
        return stationDao.findAll()
                .stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteStation(Long stationId) {
        Sections sections = new Sections(sectionDao.findByLineId(stationId));

        if (sections.isLastSection()) {
            throw new DeleteSectionException("구간이 하나인 노선에서 마지막 구간을 제거할 수 없음");
        }

        if (sections.isEmpty()) {
            return;
        }

        lineService.deleteSection(sections.getLineId(), stationId);
    }

}
