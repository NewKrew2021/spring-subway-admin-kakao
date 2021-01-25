package subway.line;

import org.springframework.stereotype.Service;
import subway.section.*;
import subway.station.Station;

import java.util.ArrayList;
import java.util.List;

@Service
public class LineService {

    private final LineDao lineDao;
    private final SectionService sectionService;

    public LineService(LineDao lineDao, SectionService sectionService) {
        this.lineDao = lineDao;
        this.sectionService = sectionService;
    }

    public boolean isLineNameExist(String lineName) {
        return lineDao.countByName(lineName) != 0;
    }

    public Line save(LineRequest lineRequest) {
        return lineDao.save(lineRequest);
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public Line findById(Long id) {
        return lineDao.findById(id);
    }

    public void modify(Long lineId, LineRequest lineRequest) {
        lineDao.modify(lineId, lineRequest);
    }

    public void deleteById(Long lineId) {
        lineDao.deleteById(lineId);
    }

    public List<Station> findStationsOfLine(Long lineId) {
        NamedSections sections = sectionService.findNamedSectionsByLineId(lineId);
        List<Station> stations = new ArrayList<>();

        NamedSection currentSection = sections.findHeadSection();
        while (currentSection.getDownStationId() != Line.TAILID) {
            stations.add(new Station(currentSection.getDownStationId(), currentSection.getDownStationName()));
            currentSection = sections.findRearByStation(currentSection.getDownStationId());
        }

        return stations;
    }

    public boolean isNotDeletable(Long lineId) {
        return !sectionService.isDeletable(lineId);
    }

    public void deleteStation(Long lineId, Long stationId) {
        Sections sections = sectionService.findSectionsForDelete(lineId, stationId);
        Section front = sections.findFrontByStation(stationId);
        Section rear = sections.findRearByStation(stationId);

        sectionService.deleteSections(sections);
        sectionService.save(new Section(
                lineId, front.getUpStationId(),
                rear.getDownStationId(),
                front.sumDistance(rear)));
    }

}
