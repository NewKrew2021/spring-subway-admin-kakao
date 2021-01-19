package subway.service;

import org.springframework.stereotype.Service;
import subway.dao.StationDao;
import subway.domain.Station;
import subway.dto.StationRequest;
import subway.dto.StationResponse;
import subway.exception.DataEmptyException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationServiceImpl implements StationService {
    private StationDao stationDao;

    public StationServiceImpl(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station save(Station station) {
        Station newStation = new Station(station.getName());
        return stationDao.save(newStation);
    }

    public List<Station> findAll() {
        List<Station> stations = stationDao.findAll();
        if(stations.size() == 0){
            throw new DataEmptyException();
        }
        return stations;
    }

    public Station findOne(Long stationId) {
        Station station = stationDao.findOne(stationId);
        if(station == null){
            throw new DataEmptyException();
        }
        return station;
    }

    public boolean deleteById(Long stationId) {
        return stationDao.deleteById(stationId) != 0;
    }

    public List<StationResponse> findAllResponse() {
        return findAll()
                .stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
    }

    public StationResponse findOneResponse(Long stationId) {
        return new StationResponse(findOne(stationId));
    }


}
