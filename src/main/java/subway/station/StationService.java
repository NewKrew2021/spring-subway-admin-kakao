package subway.station;

import org.springframework.stereotype.Service;
import subway.exceptions.stationExceptions.StationDeleteException;
import subway.exceptions.stationExceptions.StationDuplicateException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {
    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse createStation(StationRequest stationRequest) {
        if (stationDao.findByName(stationRequest.getName()) != null) {
            throw new StationDuplicateException();
        }
        return StationResponse.from(stationDao.save(stationRequest));
    }

    public List<StationResponse> getStationResponses() {
        return stationDao.findAll().stream()
                .map(station -> new StationResponse(station.getId(), station.getName()))
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
