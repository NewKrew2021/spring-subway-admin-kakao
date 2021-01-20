package subway.service;

import org.springframework.stereotype.Service;
import subway.domain.Section;
import subway.domain.Sections;
import subway.domain.Station;
import subway.repository.SectionDao;
import subway.repository.StationDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class StationService {
    private final LineService lineService;
    private final SectionService sectionService;
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public StationService(LineService lineService, SectionService sectionService, StationDao stationDao, SectionDao sectionDao) {
        this.lineService = lineService;
        this.sectionService = sectionService;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

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
        lineService.getLines().stream()
                .filter(line -> getStations(line.getId()).stream()
                        .anyMatch(station -> station.getId().equals(stationId)))
                .forEach(line -> sectionService.delete(line.getId(), stationId));

        stationDao.deleteById(stationId);
    }
}
