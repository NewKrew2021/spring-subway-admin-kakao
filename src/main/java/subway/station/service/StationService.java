package subway.station.service;

import org.springframework.stereotype.Service;
import subway.exceptions.BadRequestException;
import subway.section.domain.SectionDao;
import subway.station.domain.Station;
import subway.station.domain.StationDao;
import subway.station.presentation.StationRequest;
import subway.station.presentation.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {
    private static final String USING_STATION = "해당 지하철역은 현재 노선이나 구간에 사용중입니다.";

    private StationDao stationDao;
    private SectionDao sectionDao;

    public StationService(StationDao stationDao, SectionDao sectionDao){
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
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
        if (sectionDao.findByStationId(id).size() > 0 ){
            throw new BadRequestException(USING_STATION);
        }
        stationDao.deleteById(id);
    }
}
