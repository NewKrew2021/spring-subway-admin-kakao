package subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.domain.Line;
import subway.domain.Section;
import subway.exception.DataEmptyException;

import java.util.List;

@Service
public class LineServiceImpl implements LineService {
    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public LineServiceImpl(LineDao lineDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    @Override
    @Transactional
    public Line save(Line line, Section section) {
        Line newLine = lineDao.save(line);
        sectionDao.save(new Section(section.getUpStationId(), section.getDownStationId(), section.getDistance(), newLine.getId()));
        return newLine;
    }

    @Override
    @Transactional
    public void deleteById(Long lineId) {
        sectionDao.deleteSectionByLineId(lineId);
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
