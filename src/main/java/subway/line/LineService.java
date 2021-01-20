package subway.line;

import org.springframework.stereotype.Service;
import subway.section.Section;
import subway.section.SectionService;
import subway.station.Station;
import subway.station.StationDao;
import subway.station.StationService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {
    private LineDao lineDao;
    private StationService stationService;
    private SectionService sectionService;

    public LineService(LineDao lineDao, StationService stationService, SectionService sectionService) {
        this.lineDao = lineDao;
        this.stationService = stationService;
        this.sectionService = sectionService;
    }

    public List<Station> getStations(Long id) {
        List<Section> sections = sectionService.showAll(id);
        List<Long> stationIds = sections.stream()
                .map(Section::getUpStationId)
                .collect(Collectors.toList());
        stationIds.add(sections.get(sections.size() - 1).getDownStationId());

        return stationService.getStationsById(stationIds);
    }


    public Line createLine(Line line) {
        Line newLine = lineDao.save(line);
        Section newSection = new Section(newLine.getId(), line.getUpStationId(), line.getDownStationId(), line.getDistance());
        sectionService.createSection(newSection);
        return newLine;
    }

    public List<Line> getAllLines(){
        return lineDao.findAll();
    }

    public Line getLineById(Long lineId) {
        return lineDao.findLineById(lineId);
    }

    public void updateLineById(Long id, Line line){
        lineDao.updateById(id, line);
    }

    public void deleteLineById(Long id){
        lineDao.deleteById(id);
    }
}
