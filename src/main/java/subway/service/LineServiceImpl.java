package subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.LineDao;
import subway.domain.Line;
import subway.domain.Section;

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
        return lineDao.findAll();
    }

    @Override
    public Line findOne(Long lineId) {
        return lineDao.findOne(lineId);
    }

    @Override
    public void update(Line line) {
        lineDao.update(line);
    }
}
