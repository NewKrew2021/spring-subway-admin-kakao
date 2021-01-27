package subway.line;

import org.springframework.stereotype.Service;
import subway.section.Section;
import subway.section.SectionDao;
import subway.section.Sections;

import java.util.List;

@Service
public class LineService{
    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public LineService(LineDao lineDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    public Line save(Line line, Section section) {
        Line newLine = lineDao.save(line);
        if (newLine != null) {
            sectionDao.save(new Section(section.getUpStationId(),
                    section.getDownStationId(),
                    section.getDistance(),
                    newLine.getId()));
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

    public boolean updateAll(Line line) {
        return lineDao.updateAll(line) != 0;
    }

    public boolean update(Sections sections){
        Line line = lineDao.findOne(sections.getLineId());

        if(!sections.isFirstUpStationId(line.getUpStationId())) {
            return lineDao.updateAll(new Line(line.getId(), line.getName(), line.getColor(), sections.getFirstUpStationId(), line.getDownStationId())) != 0;
        }
        if(!sections.isLastDownStationId(line.getDownStationId())) {
            return lineDao.updateAll(new Line(line.getId(), line.getName(), line.getColor(), line.getUpStationId(), sections.getLastDownStationId())) != 0;
        }
        return false;
    }

}
