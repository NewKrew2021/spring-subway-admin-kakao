package subway.service;

import org.springframework.stereotype.Service;
import subway.dao.LineDao;
import subway.domain.Line;
import subway.dto.LineRequest;

import java.util.List;

@Service
public class LineService {
    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public Line findById(Long lineId) {
        return lineDao.findById(lineId);
    }

    public Line save(Line line) {
        return lineDao.save(line);
    }

    public boolean isDuplicateName(String name) {
        return lineDao.findByName(name) != null;
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public void deleteById(Long id) {
        lineDao.deleteById(id);
    }

    public void update(Long id, LineRequest lineRequest) {
        Line line = lineDao.findById(id);
        line.updateNameAndColor(lineRequest.getName(), lineRequest.getColor());
        lineDao.update(line);
    }
}
