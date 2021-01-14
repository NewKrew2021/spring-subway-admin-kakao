package subway.station;

import java.util.List;
import java.util.stream.Collectors;

import static subway.Container.stationDao;

public class StationService {

    public StationResponse createStation(StationRequest stationRequest) {
        Station station = new Station(stationRequest.getName());
        Station newStation = stationDao.save(station);
        return new StationResponse(newStation.getId(), newStation.getName());
    }

    public List<StationResponse> getAllStations() {
        return stationDao.findAll().stream()
                .map(station -> new StationResponse(station.getId(), station.getName()))
                .collect(Collectors.toList());
    }

    public Station findStation(long id) {
        return stationDao.findById(id);
    }

    public void deleteStation(long id) {
        stationDao.deleteById(id);
    }
}
