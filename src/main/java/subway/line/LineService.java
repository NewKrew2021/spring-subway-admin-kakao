package subway.line;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.exception.DuplicateNameException;
import subway.exception.NotExistException;
import subway.section.Section;
import subway.section.SectionService;

import java.util.List;

@Service
public class LineService {
    private final LineDao lineDao;
    private final SectionService sectionService;

    public LineService(LineDao lineDao, SectionService sectionService) {
        this.lineDao = lineDao;
        this.sectionService = sectionService;
    }

    @Transactional
    public Line createLineAndSection(Line line, int distance) {
        Line newLine = createLine(line);

        sectionService.createSection(new Section(newLine.getStartStationId(),
                newLine.getEndStationId(),
                distance,
                newLine.getId()));

        return getLine(newLine.getId());
    }

    private Line createLine(Line line) {
        validateUniqueName(line);
        return lineDao.save(line);
    }

    private void validateUniqueName(Line line) {
        if (existName(line.getName())) {
            throw new DuplicateNameException("중복된 노선 이름입니다.");
        }
    }

    public boolean existName(String name) {
        return lineDao.countByName(name) != 0;
    }

    public List<Line> getAllLines() {
        return lineDao.findAll();
    }

    public void deleteLine(long id) {
        if (lineDao.deleteById(id) == 0) {
            throw new NotExistException("해당 역이 존재하지 않습니다.");
        }
    }

    public void updateLine(long id, Line line) {
        Line newLine = getLine(id).getLineNameAndColorChanged(line.getName(), line.getColor());
        if (lineDao.updateById(id, newLine) == 0) {
            throw new NotExistException("해당 역이 존재하지 않습니다.");
        }
    }

    public Line getLine(long id) {
        Line line = lineDao.findById(id);
        if (line == null) {
            throw new NotExistException("해당 노선이 존재하지 않습니다.");
        }
        return line;
    }
}
