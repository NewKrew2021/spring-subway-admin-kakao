package subway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import subway.dao.LineDao;
import subway.domain.Line;
import subway.exception.DuplicateNameException;

import java.util.List;

@Service
public class LineService {
    private final LineDao lineDao;

    @Autowired
    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public void insertLine(Line line) {
        if (lineDao.isContainSameName(line.getName())) {
            throw new DuplicateNameException();
        }
        lineDao.save(line);
    }

    public Line findLineByName(String name) {
        return lineDao.findLineByName(name);
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public Line findById(Long id) {
        return lineDao.findById(id);
    }

    public void modifyLine(Line line) {
        lineDao.updateLine(line);
    }

    public void deleteLine(Long id) {
        lineDao.delete(id);
    }

}
