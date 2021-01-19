package subway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import subway.dao.LineDao;
import subway.dto.Line;
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

    public void insertLine(Line line) throws DuplicateLineNameException {
        if (lineDao.isContainSameName(line.getName())) {
            throw new DuplicateLineNameException();
        }
        lineDao.save(line);
    }

    public Line findLineByName(String name) throws LineNotFoundException{
        Line result = lineDao.findLineByName(name);
        if(result == null){
            throw new LineNotFoundException();
        }
        return result;
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public Line findById(Long id) {
        return lineDao.findById(id);
    }

    public void modifyLine(Line line) {
        lineDao.update(line);
    }

    public void deleteLine(Long id) {
        lineDao.delete(id);
    }

}
