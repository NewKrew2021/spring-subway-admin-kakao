package subway.line.service;

import org.springframework.stereotype.Service;
import subway.exceptions.BadRequestException;
import subway.line.domain.Line;
import subway.line.repository.LineDao;
import subway.line.dto.LineRequest;
import subway.section.domain.Section;
import subway.section.repository.SectionDao;

import java.util.List;

@Service
public class LineService {
    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public LineService(LineDao lineDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    @Transactional
    public Line saveLine(Line newLine, Long upStationId, Long downStationId, int distance) {
        Line savedLine = lineDao.save(newLine);
        sectionDao.save(new Section(savedLine.getId(),
                upStationId,
                downStationId,
                distance));
        return savedLine;
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public Line findById(Long id) {
        return lineDao.findById(id);
    }

    public void deleteById(Long id) {
        if (sectionDao.findByLineId(id).size() > 1) {
            throw new BadRequestException();
        }
        lineDao.deleteById(id);
        sectionDao.deleteByLineId(id);
    }

    public void update(Long id, Line line) {
        lineDao.update(id, line);
    }
}
