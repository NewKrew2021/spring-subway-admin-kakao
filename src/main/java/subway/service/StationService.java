package subway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import subway.dao.StationDao;
import subway.dto.Station;

import java.util.List;

@Service
public class StationService {

    @Autowired
    StationDao stationDao;

    public boolean insertSation(Station station){
        if(checkDuplicatedStationName(station.getName())){
            return false;
        }
        return stationDao.save(station)!=0;
    }

    public boolean checkDuplicatedStationName(String name){
        return stationDao.hasSameStationName(name);
    }

    public Station findStationByName(String name){
        return stationDao.findByName(name);
    }

    public List<Station> findAllStations(){
        return stationDao.findAll();
    }

    public void deleteStation(Long id){
        stationDao.deleteById(id);
    }


}
