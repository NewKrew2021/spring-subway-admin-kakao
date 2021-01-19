package subway.line;

import org.springframework.stereotype.Service;
import subway.section.Section;
import subway.section.SectionService;
import subway.station.Station;
import subway.station.StationDao;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {
    private LineDao lineDao;
    private StationDao stationDao;
    private SectionService sectionService;

    public LineService(LineDao lineDao, StationDao stationDao, SectionService sectionService) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionService = sectionService;
    }

    public List<Station> getStations(Line line) {
        List<Section> sections = sectionService.showAll(line.getId());
        List<Long> stationIds = sections.stream()
                .map(Section::getUpStationId)
                .collect(Collectors.toList());
        stationIds.add(sections.get(sections.size() - 1).getDownStationId());
        return stationIds.stream()
                .map(stationDao::findStationById)
                .collect(Collectors.toList());
    }


}
