package subway.station;

import org.springframework.stereotype.Service;
import subway.section.SectionDao;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public StationService(SectionDao sectionDao, StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public List<StationResponse> getStationsByLineId(Long lineId){
        List<Long> stations = new ArrayList<>();
        sectionDao
                .findByLineId(lineId)
                .forEach(section -> {
                    stations.add(stationDao.findById(section.getUpStationId()).getId());
                    stations.add(stationDao.findById(section.getDownStationId()).getId());
                });
        return stations.stream()
                .distinct()
                .map((Long stationId) -> stationDao.findById(stationId))
                .map((Station station) -> new StationResponse(station.getId(), station.getName()))
                .collect(Collectors.toList());
    }

    public Station saveStation(Station station){
        return stationDao.save(station);
    }

    public List<Station> getAllStations(){
        return stationDao.findAll();
    }

    public void deleteStationById(Long stationId){
        stationDao.deleteById(stationId);
    }
}
