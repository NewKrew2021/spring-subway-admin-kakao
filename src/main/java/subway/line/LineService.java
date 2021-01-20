package subway.line;

import org.springframework.stereotype.Service;
import subway.exception.DuplicateNameException;
import subway.exception.NotExistException;
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
        validateUniqueName(line);
        return lineDao.save(line);
    }

    private void validateUniqueName(Line line) {
        if (lineDao.countByName(line.getName()) != 0) {
            throw new DuplicateNameException("중복된 노선 이름입니다.");
        }
    }

    public List<Line> getAllLines() {
        return lineDao.findAll();
    }


    public void deleteLine(long id) {
        if (lineDao.deleteById(id) == 0) {
            throw new NotExistException("해당 역이 존재하지 않습니다.");
        }
    }

    public void updateLine(long id, Line line) {
        Line newLine = findById(id).getLineNameAndColorChanged(line.getName(), line.getColor());
        if (lineDao.updateById(id, newLine) == 0) {
            throw new NotExistException("해당 역이 존재하지 않습니다.");
        }
    }

    public Line getLine(long id) {
        return findById(id);
    }

    private Line findById(Long id) {
        Line line = lineDao.findById(id);
        if (line == null) {
            throw new NotExistException("해당 역이 존재하지 않습니다.");
        }
        return line;
    }
}
