package subway.line;

import org.springframework.stereotype.Service;
import subway.section.Section;

import java.util.List;

@Service
public class LineServiceImpl implements LineService {
    private final LineDao lineDao;

    public LineServiceImpl(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public Line save(Line line, Section section) {
        return lineDao.save(line, section);
    }

    public boolean deleteById(Long lineId) {
        return lineDao.deleteById(lineId) != 0;
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public Line findOne(Long lineId) {
        return lineDao.findOne(lineId);
    }

    public boolean update(Line line) {
        return lineDao.update(line) != 0;
    }

    public boolean saveSection(Long lineId, Section section) {
        return lineDao.saveSection(lineId, section);
    }

    public boolean deleteSection(Long lineId, Long stationId) {
        return lineDao.deleteSection(lineId, stationId);
    }
}
