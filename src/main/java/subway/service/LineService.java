package subway.service;

import org.springframework.stereotype.Service;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Sections;
import subway.exception.DuplicateNameException;
import subway.exception.NoContentException;
import subway.dao.StationDao;

import java.util.List;
import java.util.stream.IntStream;

@Service
public class LineService {
    private final SectionDao sectionDao;
    private final LineDao lineDao;
    private final StationDao stationDao;

    public LineService(SectionDao sectionDao, LineDao lineDao, StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.lineDao = lineDao;
        this.stationDao = stationDao;
    }

    public void checkDuplicateName(Long lineId, Long upStationId, Long downStationId) {
        if (sectionDao.findOneByLineIdAndStationId(lineId, upStationId, true) == null &&
                sectionDao.findOneByLineIdAndStationId(lineId, upStationId, false) == null &&
                sectionDao.findOneByLineIdAndStationId(lineId, downStationId, true) == null &&
                sectionDao.findOneByLineIdAndStationId(lineId, downStationId, false) == null
        ) {
            throw new DuplicateNameException(upStationId + ", " + downStationId);
        }
    }

    public Line getLine(String name, String color) {
        return lineDao.save(new Line(name,
                color
        ));
    }

    public void creatSection(Long upStationId, Long downStationId, int distance, Line line) {
        sectionDao.save(new Section(
                line.getId(),
                stationDao.findOne(upStationId),
                stationDao.findOne(downStationId),
                distance
        ));
    }

    public List<Line> findAllLines(){
        return lineDao.findAll();
    }

    public Line findOneLine(Long id){
        return lineDao.findOne(id);
    }

    public Line updateLine(Long id, Line line) {
        return lineDao.update(id, line);
    }

    public void deleteLineById(Long id) {
        lineDao.deleteById(id);
    }

    public void deleteStationOnLine(Long stationId, Section previous, Section next) {
        sectionDao.update(new Section(
                previous.getId(),
                previous.getLineId(),
                previous.getUpStation(),
                next.getDownStation(),
                previous.getDistance() + next.getDistance()
        ));
        sectionDao.deleteById(next.getId());
        stationDao.deleteById(stationId);
    }
}
