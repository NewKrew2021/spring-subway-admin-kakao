package subway.line;

import org.springframework.stereotype.Service;
import subway.exception.NotExistException;
import subway.section.SectionDao;
import subway.station.StationDao;

import java.util.List;
import java.util.stream.Collectors;

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

    public Line createLine(Line line) {
        Line newLine = lineDao.save(line);
        return newLine;
    }

    public boolean existName(String name) {
        return lineDao.countByName(name) != 0;
    }

    public void deleteLine(long id) {
        lineDao.deleteById(id);
    }

    public void updateLine(long id, String newName, String newColor) {
        Line originalLine = lineDao.findById(id);
        Line line = new Line(id, newName, newColor, originalLine.getStartStationId(), originalLine.getEndStationId());
        lineDao.updateById(id, line);
    }

    public List<LineResponse> getAllLines() {
        return lineDao.findAll().stream()
                .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor(), null))
                .collect(Collectors.toList());
    }

    public Line getLine(long id) {
        Line line = lineDao.findById(id);
        if (line == null) {
            throw new NotExistException("해당 노선이 존재하지 않습니다.");
        }
        return line;
    }
}
