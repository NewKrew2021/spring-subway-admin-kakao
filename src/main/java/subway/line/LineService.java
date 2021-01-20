package subway.line;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.section.Section;
import subway.section.SectionDto;
import subway.section.SectionService;

import java.util.List;

@Service
public class LineService {
    LineDao lineDao; //private으로 하면??
    SectionService sectionService;

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

    public List<Line> findAllLines() {
        return lineDao.findAll();
    }

    public Line findLineById(Long lineId) {
        return lineDao.findById(lineId);
    }

    public void updateLine(Long lineId, LineDto lineDto) {
        Line newLine = new Line(lineId, lineDto);
        lineDao.update(newLine);
    }

    public void deleteLineById(Long lineId) {
        lineDao.deleteById(lineId);
    }
}
