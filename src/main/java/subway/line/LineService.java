package subway.line;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import subway.line.exceptions.DuplicateLineNameException;
import subway.line.exceptions.InvalidLineDeleteException;
import subway.line.exceptions.NoSuchLineException;
import subway.station.exceptions.NoSuchStationException;

import java.util.List;
import java.util.Optional;

@Service
public class LineService {

    LineDao lineDao;

    @Autowired
    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public Line save(Line line) {
        String lineName = line.getName();
        checkDuplicateName(lineName);

        try {
            return lineDao.save(line);
        } catch (DuplicateKeyException e) {
            throw new DuplicateLineNameException(lineName);
        }
    }

    private void checkDuplicateName(String lineName) {
        if (lineDao.findByName(lineName) == null) {
            throw new DuplicateLineNameException(lineName);
        }
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public Line find(Long id) {
        return Optional.ofNullable(lineDao.findById(id))
                .orElseThrow(() -> new NoSuchStationException(id));
    }

    public void update(Line line) {
        checkExistLine(line.getId());
        lineDao.update(line);
    }

    public void delete(Long id) {
        try {
            checkExistLine(id);
            lineDao.deleteById(id);
        } catch (Exception e) {
            System.out.println(e.getClass());
            throw new InvalidLineDeleteException(id);
        }
    }

    private void checkExistLine(Long id) {
        if (lineDao.findById(id) == null) {
            throw new NoSuchLineException(id);
        }
    }
}
