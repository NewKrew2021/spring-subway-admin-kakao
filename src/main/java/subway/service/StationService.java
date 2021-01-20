package subway.service;

import org.springframework.stereotype.Service;
import subway.domain.Section;
import subway.repository.SectionDao;
import subway.domain.Sections;
import subway.domain.Station;
import subway.repository.StationDao;

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
        Sections sections = new Sections(sectionDao.findByLineId(lineId));
        List<Station> stations = new ArrayList<>();

        Map<Long, Section> orderedSections = sections.getOrderedSections();
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
