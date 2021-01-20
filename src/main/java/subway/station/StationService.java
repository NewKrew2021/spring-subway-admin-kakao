package subway.station;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {
    private StationDao stationDao;

    public StationService(StationDao stationDao){
        this.stationDao = stationDao;
    }

    public List<Station> getStationsById(List<Long> stationIds){
        return stationIds.stream()
                .map(stationDao::findStationById)
                .collect(Collectors.toList());
    }


}
