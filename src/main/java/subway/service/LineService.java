package subway.service;

import org.springframework.stereotype.Service;
import subway.dao.LineDao;
import subway.domain.Line;
import subway.exception.DuplicateException;
import subway.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {
    private LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public Line createLine(Line line) {
        if(lineDao.hasDuplicateName(line.getName())) throw new DuplicateException();
        return lineDao.save(line);
    }

    public Line getLine(Long id) {
        return lineDao.findById(id).orElseThrow(NotFoundException::new);
    }

    public List<Line> getLines() {
        return lineDao.findAll()
                .stream()
                .collect(Collectors.toList());
    }

    public void updateLine(Line line) {
        lineDao.update(line);
    }

    public void deleteLine(Long id) {
        lineDao.deleteById(id);
    }
}
