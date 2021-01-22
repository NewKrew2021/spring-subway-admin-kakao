package subway.service;

import org.springframework.stereotype.Service;
import subway.domain.Line;
import subway.repository.LineDao;
import subway.repository.SectionDao;

import java.util.List;

@Service
public class LineService {
    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public LineService(LineDao lineDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    public Long create(Line line) {
        return lineDao.save(line);
    }

    public List<Line> getLines() {
        return lineDao.findAll();
    }

    public void delete(Long lineId) {
        sectionDao.deleteByLineId(lineId);
        lineDao.deleteById(lineId);
    }

    public Line getLine(Long lineId) {
        return lineDao.findById(lineId);
    }

    public void update(Line line) {
        lineDao.update(line);
    }
}
