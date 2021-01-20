package subway.station.service;

import org.springframework.stereotype.Service;
import subway.exceptions.DuplicateNameException;
import subway.station.dao.StationDao;
import subway.station.domain.Station;
import subway.station.dto.StationRequest;
import subway.station.dto.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {

    private StationDao stationDao;

    public StationService (StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse createLine(StationRequest stationRequest) {
        if(stationDao.countByName(stationRequest.getName()) != 0) {
            throw new DuplicateNameException("이미 존재하는 역입니다.");
        }
        Station newStation = stationDao.save(stationRequest.getName());
        return new StationResponse(newStation);
    }

    public List<StationResponse> showStations() {
        return stationDao.findAll()
                .stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
    }

    public void deleteStation(Long id) {
        stationDao.deleteById(id);
    }
}
