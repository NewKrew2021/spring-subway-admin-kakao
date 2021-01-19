package subway.line;

import org.springframework.stereotype.Service;
import subway.section.*;

import java.util.LinkedList;
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

    public Long requestToLine(LineDto lineDto) {
        Line line = new Line(lineDto.getName(), lineDto.getColor());
        return lineDao.save(line);
    }

    public void createTerminalSections(LineDto lineDto, Long lineId) {
        Section upTerminalSection = new Section(lineId, lineDto.getUpStationId(), lineDto.getDistance(), lineDto.getDownStationId());
        Section downTerminalSection = new Section(lineId, lineDto.getDownStationId(), 0, Section.WRONG_ID);
        sectionDao.save( upTerminalSection );
        sectionDao.save( downTerminalSection );
    }

    public LinkedList<Long> getStationsIdOfLine(long lineId) {
        List<Section> sections = sectionDao.getSectionsOfLine(lineId)
                .stream()
                .collect(Collectors.toList());
        return sortSections(sections);
    }

    private LinkedList<Long> sortSections(List<Section> sections) {
        LinkedList<Long> linkedList = new LinkedList<>();
        Section currentSection = findSectionByNextId(sections, Section.WRONG_ID);
        while(linkedList.size() != sections.size()) {
            linkedList.addFirst(currentSection.getStationId());
            currentSection = findSectionByNextId(sections, currentSection.getStationId());
        }
        return linkedList;
    }

    private Section findSectionByNextId(List<Section> sections, Long nextId) {
        return sections.stream()
                .filter(section -> section.getNextStationId() == nextId)
                .findAny()
                .orElse(null);
    }
}
