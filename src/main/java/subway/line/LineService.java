package subway.line;

import org.springframework.stereotype.Service;
import subway.station.Station;
import subway.station.StationDao;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {
    private LineDao lineDao;
    private StationDao stationDao;

    public LineService(LineDao lineDao, StationDao stationDao){
        this.lineDao = lineDao;
        this.stationDao = stationDao;
    }

    public List<Station> getStations(Line line){
        List<Long> stationIds = line.getStationIds();
        return stationIds.stream()
                .map(stationDao::findStationById)
                .collect(Collectors.toList());
    }


}
