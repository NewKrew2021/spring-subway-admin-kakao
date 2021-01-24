package subway.line;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import subway.line.domain.Line;
import subway.line.vo.LineAttributes;
import subway.line.vo.LineCreateValue;
import subway.line.vo.LineResultValue;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class LineService {
    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public LineResultValue create(LineCreateValue createValue) {
        Line line;

        try {
            line = lineDao.insert(new Line(createValue.getName(), createValue.getColor()));
        } catch (DataAccessException e) {
            throw new IllegalArgumentException(
                    String.format("%s\nCould not create line. Line with name %s already exists",
                            e.getMessage(), createValue.getName()));
        }

        return line.toResultValue();
    }

    public List<LineResultValue> findAll() {
        return lineDao.findAll()
                .stream()
                .map(Line::toResultValue)
                .collect(Collectors.toList());
    }

    public LineResultValue findByID(long lineID) {
        Line line;

        try {
            line = lineDao.findOne(lineID);
        } catch (DataAccessException e) {
            throw new NoSuchElementException(
                    String.format("%s\nCould not find line with id: %d",
                            e.getMessage(), lineID));
        }

        return line.toResultValue();
    }

    public LineResultValue update(long lineID, LineAttributes attributes) {
        Line line;

        try {
            line = lineDao.findOne(lineID);
        } catch (DataAccessException e) {
            throw new NoSuchElementException(
                    String.format("%s\nCould not retrieve line with id: %d",
                            e.getMessage(), lineID));
        }

        line.changeAttributesToNameAndColor(attributes.getName(), attributes.getColor());

        try {
            lineDao.update(line);
        } catch (DataAccessException e) {
            throw new IllegalArgumentException(
                    String.format("%s\nCould not update line. Line with name %s already exists",
                            e.getMessage(), attributes.getName()));
        }

        return line.toResultValue();
    }

    public void delete(long lineID) {
        lineDao.delete(lineID);
    }
}
