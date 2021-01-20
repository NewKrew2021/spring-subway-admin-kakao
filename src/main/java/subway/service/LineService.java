package subway.service;

import org.springframework.stereotype.Service;
import subway.domain.Line;
import subway.repository.LineDao;

import javax.annotation.Resource;
import java.util.List;

@Service
public class LineService {

    @Resource
    private LineDao lineDao;

    public Long create(Line line) {
        return lineDao.save(line);
    }

    public List<Line> getLines() {
        return lineDao.findAll();
    }

    public void delete(Long id) {
        lineDao.deleteById(id);
    }

    public Line getLine(Long lineId) {
        return lineDao.findById(lineId);
    }

    public void update(Line line) {
        lineDao.update(line);
    }
}
