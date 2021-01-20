package subway.line;

import org.springframework.stereotype.Service;
import subway.section.Section;
import subway.section.SectionDao;
import subway.section.SectionService;
import subway.station.Station;
import subway.station.StationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class LineService {
    private LineDao lineDao;
    private StationService stationService;
    private SectionDao sectionDao;

    public LineService(LineDao lineDao, StationService stationService, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.stationService = stationService;
        this.sectionDao = sectionDao;
    }

    public List<Station> getStations(Long id) {
        List<Section> sections = sortedSectionsByLineId(id);
        List<Long> stationIds = sections.stream()
                .map(Section::getUpStationId)
                .collect(Collectors.toList());
        stationIds.add(sections.get(sections.size() - 1).getDownStationId());

        return stationService.getStationsById(stationIds);
    }

    public List<Section> sortedSectionsByLineId(Long lineId) {
        Map<Long, Section> sections = sectionDao.findByLineId(lineId).stream()
                .collect(Collectors.toMap(Section::getUpStationId, Function.identity()));

        return getSortedSections(lineId, sections);
    }

    private List<Section> getSortedSections(Long lineId, Map<Long, Section> sections) {
        Line line = lineDao.findLineById(lineId);
        Long headStationId = line.getUpStationId();
        Long tailStationId = line.getDownStationId();

        List<Section> result = new ArrayList<>();
        Section section;
        Long iterStationId = headStationId;
        do {
            section = sections.get(iterStationId);
            result.add(section);
            iterStationId = section.getDownStationId();
        } while (iterStationId != tailStationId);

        return result;
    }

    public Line createLine(Line line) {
        Line newLine = lineDao.save(line);
        Section newSection = new Section(newLine.getId(), line.getUpStationId(), line.getDownStationId(), line.getDistance());
        sectionDao.insert(newSection);
        return newLine;
    }

    public List<Line> getAllLines(){
        return lineDao.findAll();
    }

    public Line getLineById(Long lineId) {
        return lineDao.findLineById(lineId);
    }

    public void updateLine(Line line){
        lineDao.update(line);
    }

    public void deleteLineById(Long id){
        lineDao.deleteById(id);
    }
}
