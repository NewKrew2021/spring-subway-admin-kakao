package subway.service;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.LineDao;
import subway.domain.line.Line;
import subway.domain.section.Section;
import subway.domain.section.Sections;
import subway.exception.id.InvalidLineIdException;

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

    @Transactional
    public Line createLine(Line line, Section section) {
        Line newLine = lineDao.save(line);
        Section newSection = new Section(null, newLine.getId(), section.getUpStation(), section.getDownStation(), section.getDistance());
        sectionService.save(newSection);
        return showLine(newLine.getId());
    }

    @Transactional(readOnly = true)
    public List<Line> showLines() {
        return lineDao.findAll().stream()
                .map(line -> new Line(line, sectionService.getSectionsByLineId(line.getId())))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Line showLine(Long id) {
        Line line;
        try {
            line = lineDao.getById(id);
        } catch(EmptyResultDataAccessException e) {
            throw new InvalidLineIdException(id);
        }
        Sections sections = sectionService.getSectionsByLineId(id);
        return new Line(line, sections);
    }

    @Transactional
    public void modifyLine(Long id, Line line) {
        validateId(id);
        lineDao.update(id, line);
    }

    @Transactional
    public void deleteLine(Long id) {
        validateId(id);
        lineDao.deleteById(id);
    }

    private void validateId(Long id) {
        if(!lineDao.contain(id)) {
            throw new InvalidLineIdException(id);
        }
    }
}
