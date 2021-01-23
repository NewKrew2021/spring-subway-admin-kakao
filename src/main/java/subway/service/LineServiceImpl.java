package subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.LineDao;
import subway.domain.Line;
import subway.domain.Section;
import subway.exception.DataEmptyException;

import java.util.List;

@Service
public class LineServiceImpl implements LineService {
    private final LineDao lineDao;
    private final SectionService sectionService;

    public LineServiceImpl(LineDao lineDao, SectionService sectionService) {
        this.lineDao = lineDao;
        this.sectionService = sectionService;
    }

    @Override
    @Transactional
    public Line save(Line line, Section section) {
        return lineDao.save(line, section);
    }

    @Override
    @Transactional
    public void deleteById(Long lineId) {
        sectionService.deleteSectionByLineId(lineId);
        lineDao.deleteById(lineId);
    }

    @Override
    public List<Line> findAll() {
        List<Line> lines = lineDao.findAll();
        if (lines.size() == 0) {
            throw new DataEmptyException();
        }
        return lines;
    }

    @Override
    public Line findOne(Long lineId) {
        Line line = lineDao.findOne(lineId);
        if (line == null) {
            throw new DataEmptyException();
        }
        return line;
    }

    @Override
    public void update(Line line) {
        lineDao.update(line);
    }
}
