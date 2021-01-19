package subway.line;

import org.springframework.stereotype.Service;
import subway.exception.InvalidIdException;
import subway.section.Section;
import subway.section.SectionService;
import subway.section.Sections;

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
        Line line = lineDao.getById(id);
        if (line == null) {
            throw new InvalidIdException("존재하지 않는 Line ID 입니다. Line ID : " + id);
        }

        Sections sections = sectionService.getSectionsByLineId(id);
        return new Line(line, sections);
    }

    public void modifyLine(Long id, Line line) {
        lineDao.update(id, line);
    }

    public void deleteLine(Long id) {
        if(!lineDao.deleteById(id)) {
            throw new InvalidIdException("존재하지 않는 Line ID 입니다. Line ID : " + id);
        }
    }
}
