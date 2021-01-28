package subway.station;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {
    private StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public List<Station> getStationsById(List<Long> stationIds) {
        return stationIds.stream()
                .map(stationDao::findStationById)
                .collect(Collectors.toList());
    }

    public Station createStation(Station station) {
        return stationDao.save(station);
    }

    public List<Station> getStations() {
        return stationDao.findAll();
    }

    public void deleteStationById(Long id) {
        stationDao.deleteStationById(id);
    }

}
