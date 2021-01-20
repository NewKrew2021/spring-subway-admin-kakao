package subway.station.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import subway.section.dao.SectionDao;
import subway.station.dao.StationDao;
import subway.station.domain.Station;
import subway.station.domain.StationRequest;
import subway.station.domain.StationResponse;
import subway.station.domain.Stations;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {

    private final StationDao stationDao;
    private final SectionDao sectionDao;

    @Autowired
    public StationService(StationDao stationDao, SectionDao sectionDao) {
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    public StationResponse createStation(StationRequest stationRequest) {
        stationDao.save(new Station(stationRequest.getName()));

        Station newStation = stationDao.findByName(stationRequest.getName());
        return new StationResponse(newStation.getId(), newStation.getName());
    }

    public List<StationResponse> showStations() {
        Stations stations = new Stations(stationDao.findAll());
        return stations.getStations()
                .stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
    }

    public void deleteStation(Long id) {
        stationDao.deleteById(id);
    }

}
