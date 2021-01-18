package subway.line;

import org.springframework.stereotype.Service;
import subway.section.Section;
import subway.section.SectionDao;
import subway.section.Sections;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public LineService(LineDao lineDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    public Long requestToLine(String name, String color) {
        Line line = new Line(name, color);
        return lineDao.save(line);
    }

    public void createTerminalSections(Long upStationId, Long downStationId, int distance, Long lineId) {
        Section upTerminalSection = new Section(lineId, 0, upStationId, distance, downStationId);
        Section downTerminalSection = new Section(lineId, distance, upStationId, distance, null);
        sectionDao.save( upTerminalSection );
        sectionDao.save( downTerminalSection );
    }

    public List<Long> getStationsId(long lineId) {
        return sectionDao.getSectionsOfLine(lineId)
                .stream()
                .map(Section::getStationId)
                .collect(Collectors.toList());
    }
}
