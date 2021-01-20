package subway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Station;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.domain.SectionGroup;
import subway.dao.StationDao;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {

    private final StationDao stationDao;
    private final LineDao lineDao;
    private final SectionDao sectionDao;

    @Autowired
    public LineService(StationDao stationDao, LineDao lineDao, SectionDao sectionDao) {
        this.stationDao = stationDao;
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    public Line create(Line line, Section section) {
        Line newLine = lineDao.save(line);
        SectionGroup sections = SectionGroup.insertFirstSection(newLine.getId(),
                section.getUpStationId(),
                section.getDownStationId(),
                section.getDistance());
        sectionDao.saveAll(sections);
        return newLine;
    }

    public List<Line> showAll() {
        return lineDao.findAll();
    }

    public Line showLine(Long id) {
        return lineDao.findOne(id);
    }

    public List<Station> showStationsByLineId(Long id) {
        SectionGroup sections = new SectionGroup(sectionDao.findAllByLineId(id));
        return sections.getAllStationId().stream()
                .map(stationDao::findOne)
                .collect(Collectors.toList());
    }

    public void update(Line line) {
        lineDao.update(line);
    }

    public void deleteById(Long id) {
        lineDao.deleteById(id);
    }
}
