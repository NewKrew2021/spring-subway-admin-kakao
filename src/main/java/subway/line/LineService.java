package subway.line;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import subway.line.exceptions.DuplicateLineNameException;
import subway.line.exceptions.InvalidLineDeleteException;
import subway.line.exceptions.NotFoundLineException;

import java.util.List;

@Service
public class LineService {

    LineDao lineDao;

    @Autowired
    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public Line save(Line line) {
        String lineName = line.getName();
        if (lineDao.isDuplicateName(lineName)) {
            throw new DuplicateLineNameException(lineName);
        }

        try {
            return lineDao.save(line);
        } catch (DuplicateKeyException e) {
            throw new DuplicateLineNameException(lineName);
        }
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public Line find(Long id) {
        try {
            return lineDao.findById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundLineException(id);
        }
    }

    public void update(Line line) {
        if (!lineDao.isExistLine(line.getId())) {
            throw new NotFoundLineException(line.getId());
        }
        lineDao.update(line);
    }

    public void delete(Long id) {
        try {
            lineDao.deleteById(id);
        } catch (Exception e) {
            throw new InvalidLineDeleteException(id);
        }
    }
}
