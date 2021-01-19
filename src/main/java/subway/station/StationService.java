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

    public StationResponse createStation(StationRequest stationRequest){
        Station station = new Station(stationRequest.getName());
        Station newStation = stationDao.save(station);
        return new StationResponse(newStation.getId(), newStation.getName());
    }

    public List<StationResponse> showStations(){
        return stationDao.findAll().stream()
                .map((Station station) -> new StationResponse(station.getId(), station.getName()))
                .collect(Collectors.toList());
    }

    public void deleteById(Long id){
        stationDao.deleteById(id);
    }
}
