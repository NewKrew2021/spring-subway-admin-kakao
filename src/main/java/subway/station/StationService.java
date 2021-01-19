package subway.station;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import subway.line.Line;
import subway.line.LineDao;
import subway.line.Section;
import subway.line.SectionDao;

import javax.annotation.Resource;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class StationService {

    @Resource
    public StationDao stationDao;

    @Resource
    public LineDao lineDao;

    @Resource
    public SectionDao sectionDao;

    public ResponseEntity<StationResponse> create(StationRequest stationRequest) {
        try {
            stationDao.save(new Station(stationRequest.getName()));
        } catch (DuplicateKeyException e) {
            return ResponseEntity.badRequest().build();
        }

        Station newStation = stationDao.findByName(stationRequest.getName());

        return ResponseEntity.created(URI.create("/stations/" + newStation.getId()))
                .body(new StationResponse(newStation.getId(), newStation.getName()));
    }

    public ResponseEntity<List<StationResponse>> getStations() {
        List<Station> stations = stationDao.findAll();

        return ResponseEntity.ok().body(StationResponse.getStationResponses(stations));
    }

    public List<Station> getStations(Long lineId) {
        Line line = lineDao.findById(lineId);
        List<Section> sections = sectionDao.findByLineId(lineId);
        List<Station> stations = new ArrayList<>();

        Map<Long, Section> orderedSections = Section.getOrderedSections(sections);
        Long upStationId = line.getUpStationId();
        stations.add(stationDao.findById(upStationId));

        while (orderedSections.containsKey(upStationId)) {
            Section section = orderedSections.get(upStationId);
            stations.add(stationDao.findById(section.getDownStationId()));
            upStationId = section.getDownStationId();
        }

        return stations;
    }

    public ResponseEntity delete(Long id) {
        stationDao.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
