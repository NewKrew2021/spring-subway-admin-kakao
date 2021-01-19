package subway.line;

import org.springframework.stereotype.Service;
import subway.exception.NotExistException;
import subway.section.Section;
import subway.section.SectionDao;
import subway.station.StationDao;

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

    public Line createLine(Line line, int distance) {
        Line newLine = lineDao.save(line);

        Section section = new Section(newLine.getStartStationId(), newLine.getEndStationId(), distance, newLine.getId());
        sectionDao.save(section);

        return newLine;
    }

    public boolean existName(String name) {
        return lineDao.countByName(name) != 0;
    }

    public void deleteLine(long id) {
        lineDao.deleteById(id);
    }

    public void updateLine(long id, Line line) {
        Line originalLine = lineDao.findById(id);
        lineDao.updateById(id, originalLine.getLineNameAndColorChanged(line.getName(), line.getColor()));
    }

    public List<Line> getAllLines() {
        return lineDao.findAll();
    }

    public Line getLine(long id) {
        Line line = lineDao.findById(id);
        if (line == null) {
            throw new NotExistException("해당 노선이 존재하지 않습니다.");
        }
        return line;
    }
}
