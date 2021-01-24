package subway.line;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import subway.line.domain.Line;
import subway.line.vo.LineAttributes;
import subway.line.vo.LineCreateValue;
import subway.line.vo.LineResultValue;
import subway.section.SectionService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class LineService {
    private final LineDao lineDao;
    private final SectionService sectionService;

    public LineService(LineDao lineDao, SectionService sectionService) {
        this.lineDao = lineDao;
        this.sectionService = sectionService;
    }

    public LineResultValue create(LineCreateValue createValue) {
        Line line = insert(new Line(createValue.getName(), createValue.getColor()));
        return LineResultValue.of(line, sectionService.findStationValuesByLine(line));
    }

    public List<LineResultValue> findAll() {
        return lineDao.findAll()
                .stream()
                .map(Line::toResultValue)
                .collect(Collectors.toList());
    }

    public LineResultValue findByID(long lineID) {
        Line line = findOneBy(lineID);
        return LineResultValue.of(line, sectionService.findStationValuesByLine(line));
    }

    public LineResultValue update(long lineID, LineAttributes attributes) {
        Line line = findOneBy(lineID);

        line.changeAttributesToNameAndColor(attributes.getName(), attributes.getColor());
        update(line);

        return LineResultValue.of(line, sectionService.findStationValuesByLine(line));
    }

    public void delete(long lineID) {
        lineDao.delete(lineID);
    }

    private Line insert(Line line) {
        try {
            return lineDao.insert(line);
        } catch (DataAccessException e) {
            throw new IllegalArgumentException(
                    String.format("%s\nCould not create line. Line with name %s already exists",
                            e.getMessage(), line.getName()));
        }
    }

    private Line findOneBy(long lineID) {
        try {
            return lineDao.findOne(lineID);
        } catch (DataAccessException e) {
            throw new NoSuchElementException(
                    String.format("%s\nCould not find line with id: %d",
                            e.getMessage(), lineID));
        }
    }

    private void update(Line line) {
        try {
            lineDao.update(line);
        } catch (DataAccessException e) {
            throw new IllegalArgumentException(
                    String.format("%s\nCould not update line. Line with name %s already exists",
                            e.getMessage(), line.getName()));
        }
    }
}
