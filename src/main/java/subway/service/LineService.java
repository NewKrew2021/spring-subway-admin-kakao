package subway.service;

import org.springframework.stereotype.Service;
import subway.dao.LineDao;
import subway.domain.line.Line;
import subway.domain.line.LineResponse;
import subway.exception.NotExistException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {
    static final String NOT_EXIST_LINE_ERROR_MESSAGE = "해당 노선이 존재하지 않습니다.";
    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public Line createLine(String lineName, String lineColor) {
        Line newLine = lineDao.save(new Line(lineName,lineColor));
        return newLine;
    }

    public void deleteLine(long id) {
        lineDao.deleteById(id);
    }

    public void updateLine(long id, String newName, String newColor) {
        Line line = new Line(id, newName, newColor);
        lineDao.updateById(id, line);
    }

    public List<LineResponse> getAllLines() {
        return lineDao.findAll().stream()
                .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor(), null))
                .collect(Collectors.toList());
    }

    public Line getLine(long id) {
        Line line = lineDao.findById(id);
        if (line == null) {
            throw new NotExistException(NOT_EXIST_LINE_ERROR_MESSAGE);
        }
        return line;
    }
}
