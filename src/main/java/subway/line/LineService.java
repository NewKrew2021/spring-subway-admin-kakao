package subway.line;

import org.springframework.stereotype.Service;
import subway.station.Station;
import subway.station.StationDao;

import java.util.ArrayList;
import java.util.List;

@Service
public class LineService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public LineService(LineDao lineDao, SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public boolean isLineNameExist(String lineName) {
        return lineDao.findByName(lineName) != 0;
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
        Sections sections = sectionDao.findSectionsByLineId(lineId);
        List<Station> stations = new ArrayList<>();

        Section currentSection = sections.findHeadSection();
        while (currentSection.getDownStationId() != Line.TAIL) {
            stations.add(stationDao.findById(currentSection.getDownStationId()));
            currentSection = sections.findRearOfGivenSection(currentSection.getDownStationId());
        }

        return stations;
    }

    public boolean canNotDelete(Long lineId) {
        return sectionDao.findSectionsByLineId(lineId).size() <= 3;
    }

    public void deleteStation(Long lineId, Long stationId) {
        Sections sections = sectionDao.findSectionsForDelete(lineId, stationId);
        Section front = sections.findFrontOfGivenStation(stationId);
        Section rear = sections.findRearOfGivenSection(stationId);

        sectionDao.deleteSections(sections);

        int distance = zeroIfOneOfDistanceIsVirtual(front.getDistance(), rear.getDistance());
        sectionDao.save(new Section(lineId, front.getUpStationId(), rear.getDownStationId(), distance));
    }

    private int zeroIfOneOfDistanceIsVirtual(int distance1, int distance2) {
        return Math.min(distance1, distance2) == Section.VIRTUAL_DISTANCE
                ? Section.VIRTUAL_DISTANCE
                : distance1 + distance2;
    }

}
