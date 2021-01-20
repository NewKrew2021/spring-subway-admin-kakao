package subway.station;

import org.springframework.stereotype.Service;
import subway.line.Section;
import subway.line.SectionDao;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class StationService {

    @Resource
    public StationDao stationDao;
    @Resource
    public SectionDao sectionDao;

    public Long create(Station station) {
        return stationDao.save(station);
    }

    public List<Station> getStations() {
        return stationDao.findAll();
    }

    public List<Station> getStations(Long lineId) {
        List<Section> sections = sectionDao.findByLineId(lineId);
        List<Station> stations = new ArrayList<>();

        Map<Long, Section> orderedSections = Section.getOrderedSections(sections);
        Long upStationId = sectionDao.findFirstByLineId(lineId).getUpStationId();
        stations.add(stationDao.findById(upStationId));

        while (orderedSections.containsKey(upStationId)) {
            Section section = orderedSections.get(upStationId);
            stations.add(stationDao.findById(section.getDownStationId()));
            upStationId = section.getDownStationId();
        }

        return stations;
    }

    public void delete(Long stationId) {
        stationDao.deleteById(stationId);
    }
}
