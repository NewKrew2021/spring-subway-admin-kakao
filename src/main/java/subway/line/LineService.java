package subway.line;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.section.SectionDto;
import subway.section.SectionService;

import java.util.List;

@Service
public class LineService {
    private final LineDao lineDao;
    private final SectionService sectionService;

    public LineService(LineDao lineDao, SectionService sectionService){
        this.lineDao = lineDao;
        this.sectionService = sectionService;
    }

    @Transactional
    public Line createLine(LineDto lineDto, SectionDto sectionDto) {
        Line newLine = lineDao.save(new Line(lineDto));
        sectionService.createSection(newLine.getId(), sectionDto);
        return newLine;
    }

    @Transactional
    public List<Line> findAllLines() {
        return lineDao.findAll();
    }

    @Transactional
    public Line findLineById(Long lineId) {
        return lineDao.findById(lineId);
    }

    @Transactional
    public void updateLine(Long lineId, LineDto lineDto) {
        Line newLine = new Line(lineId, lineDto);
        lineDao.update(newLine);
    }

    @Transactional
    public void deleteLineById(Long lineId) {
        lineDao.deleteById(lineId);
    }
}
