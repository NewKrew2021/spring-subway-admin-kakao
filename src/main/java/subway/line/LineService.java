package subway.line;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import subway.exceptions.DuplicateNameException;
import subway.exceptions.InvalidDeleteException;

import java.util.List;

@Service
public class LineService {

    LineDao lineDao;

    @Autowired
    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public Line save(Line line) throws DuplicateNameException {
        try {
            return lineDao.save(line);
        } catch (DuplicateNameException e) {
            throw new DuplicateNameException("중복된 이름의 Line은 추가할 수 없습니다.");
        }
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public Line find(Long id) throws EmptyResultDataAccessException {
        try {
            return lineDao.findById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new EmptyResultDataAccessException("해당하는 Line을 찾을 수 없습니다.", 1);
        }
    }

    public void update(Line line) {
        lineDao.update(line);
    }

    public void delete(Long id) throws InvalidDeleteException {
        if (lineDao.deleteById(id) == 0) {
            throw new InvalidDeleteException("삭제하려는 Line이 존재하지 않습니다.");
        }
    }
}
