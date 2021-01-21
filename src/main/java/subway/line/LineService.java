package subway.line;

import org.springframework.stereotype.Service;
import subway.line.domain.Line;
import subway.line.vo.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LineService {
    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public LineResultValue create(LineCreateValue createValue) {
        Line foundLine = lineDao.insert(new Line(createValue.getName(), createValue.getColor()));

        Line line = Optional.ofNullable(foundLine)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Line with name %s already exists", createValue.getName())));

        return line.toResultValue();
    }

    public List<LineResultValue> findAll() {
        return lineDao.findAll()
                .stream()
                .map(Line::toResultValue)
                .collect(Collectors.toList());
    }

    public LineResultValue findByID(LineReadValue readValue) {
        Line foundLine = lineDao.findOne(new Line(readValue.getID()));

        Line line = Optional.ofNullable(foundLine)
                .orElseThrow(() -> new NoSuchElementException(
                        String.format("Could not find line with id: %d", readValue.getID())));

        return line.toResultValue();
    }

    public LineResultValue update(LineUpdateValue updateValue) {
        Line line = lineDao.update(new Line(updateValue.getID(), updateValue.getName(), updateValue.getColor()));

        if (isNotUpdated(line)) {
            throw new NoSuchElementException(String.format("Could not update line with id: %d", updateValue.getID()));
        }

        return line.toResultValue();
    }

    public void delete(LineDeleteValue deleteValue) {
        Line line = lineDao.delete(new Line(deleteValue.getID()));

        if (isNotDeleted(line)) {
            throw new NoSuchElementException(String.format("Could not delete line with id: %d", line.getID()));
        }
    }

    private boolean isNotUpdated(Line line) {
        return line == null;
    }

    private boolean isNotDeleted(Line line) {
        return line != null;
    }
}
