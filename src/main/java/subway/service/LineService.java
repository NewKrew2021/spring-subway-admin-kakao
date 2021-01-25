package subway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import subway.dao.LineDao;
import subway.domain.Line;
import subway.exception.DuplicateLineNameException;
import subway.exception.LineNotFoundException;

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
            throw new DuplicateLineNameException();
        }
        lineDao.save(line);
    }

    public Line findLineByName(String name) {
        return lineDao.findLineByName(name).orElseThrow(LineNotFoundException::new);
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public Line findById(Long id) {
        return lineDao.findById(id).orElseThrow(LineNotFoundException::new);
    }

    public void modifyLine(Line line) {
        lineDao.update(line);
    }

    public void deleteLine(Long id) {
        lineDao.delete(id);
    }

}
