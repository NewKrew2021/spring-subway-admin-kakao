package subway.service;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import subway.dao.LineDao;
import subway.exception.InvalidIdException;
import subway.domain.line.Line;
import subway.domain.section.Section;
import subway.domain.section.Sections;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {
    private final LineDao lineDao;
    private final SectionService sectionService;

    public LineService(LineDao lineDao, SectionService sectionService) {
        this.lineDao = lineDao;
        this.sectionService = sectionService;
    }

    public Line createLine(Line line, Section section) {
        Line newLine = lineDao.save(line);
        Section newSection = new Section(null, newLine.getId(), section.getUpStation(), section.getDownStation(), section.getDistance());
        sectionService.save(newSection);
        return showLine(newLine.getId());
    }

    public List<Line> showLines() {
        return lineDao.findAll().stream()
                .map(line -> new Line(line, sectionService.getSectionsByLineId(line.getId())))
                .collect(Collectors.toList());
    }

    public Line showLine(Long id) {
        Line line;
        try {
            line = lineDao.getById(id);
        } catch(EmptyResultDataAccessException e) {
            throw new InvalidIdException(InvalidIdException.INVALID_LINE_ID_ERROR + id);
        }
        Sections sections = sectionService.getSectionsByLineId(id);
        return new Line(line, sections);
    }

    public void modifyLine(Long id, Line line) {
        validateId(id);
        lineDao.update(id, line);
    }

    public void deleteLine(Long id) {
        validateId(id);
        lineDao.deleteById(id);
    }

    private void validateId(Long id) {
        if(!lineDao.contain(id)) {
            throw new InvalidIdException(InvalidIdException.INVALID_LINE_ID_ERROR + id);
        }
    }
}
