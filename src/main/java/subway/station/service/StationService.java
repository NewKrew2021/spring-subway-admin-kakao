package subway.station.service;

import org.springframework.stereotype.Service;
import subway.exceptions.stationExceptions.StationDeleteException;
import subway.exceptions.stationExceptions.StationDuplicateException;
import subway.station.domain.Station;
import subway.station.domain.StationDao;
import subway.station.presentation.StationRequest;
import subway.station.presentation.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {
    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse createStation(StationRequest stationRequest) {
        Station station = new Station(stationRequest.getName());
        if (stationDao.findByName(station.getName()) != null) {
            throw new StationDuplicateException();
        }
        return StationResponse.from(stationDao.save(station));
    }

    public List<StationResponse> getStationResponses() {
        return stationDao.findAll().stream()
                .map(StationResponse::from)
                .collect(Collectors.toList());
    }

    public void deleteStationById(Long StationId) {
        if (stationDao.findById(StationId) == null) {
            throw new StationDeleteException();
        }
        stationDao.deleteById(StationId);
    }

    public StationResponse getStationResponseById(Long stationId) {
        return StationResponse.from(stationDao.findById(stationId));
    }

}
