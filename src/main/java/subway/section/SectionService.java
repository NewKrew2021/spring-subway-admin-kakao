package subway.section;

import subway.exception.NotExistException;
import subway.line.Line;
import subway.station.Station;
import subway.station.StationResponse;
import subway.station.StationService;

import java.util.ArrayList;
import java.util.List;

import static subway.Container.sectionDao;
import static subway.Container.stationDao;

public class SectionService {
    private final StationService stationService;

    public SectionService() {
        this.stationService = new StationService();
    }

    public void createSection(Section section) {
        sectionDao.save(section);
    }

    public List<StationResponse> getStationsOfLine(Line line) {
        List<StationResponse> stations = new ArrayList<>();
        Station curStation = stationService.findStation(line.getStartStationId());
        Section curSection = sectionDao.findByUpStationId(curStation.getId());
        while (curSection != null) {
            stations.add(new StationResponse(curStation.getId(), curStation.getName()));
            curStation = stationService.findStation(curSection.getDownStationId());
            curSection = sectionDao.findByUpStationId(curStation.getId());
        }
        stations.add(new StationResponse(curStation.getId(), curStation.getName()));
        return stations;
    }
}
