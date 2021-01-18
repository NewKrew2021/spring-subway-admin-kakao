package subway.line;

import org.springframework.stereotype.Service;
import subway.section.Section;
import subway.section.SectionDao;
import subway.section.SectionService;

import java.util.List;

@Service
public class LineServiceImpl implements LineService {
    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public LineServiceImpl(LineDao lineDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    public Line save(Line line, Section section) {
        Line newLine = lineDao.save(line);
        if (newLine != null) {
            sectionDao.save(new Section(section.getUpStationId(), section.getDownStationId(), section.getDistance(), newLine.getId()));
        }
        return newLine;
    }

    public boolean deleteById(Long lineId) {
        return lineDao.deleteById(lineId) != 0;
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public Line findOne(Long lineId) {
        return lineDao.findOne(lineId);
    }

    public boolean update(Line line) {
        return lineDao.update(line) != 0;
    }

    public boolean updateAll(Line line){
        return lineDao.updateAll(line) != 0;
    }

}
