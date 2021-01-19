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

    public Long requestToLine(String name, String color) {
        Line line = new Line(name, color);
        return lineDao.save(line);
    }

    public void createTerminalSections(Long upStationId, Long downStationId, int distance, Long lineId) {
        Section upTerminalSection = new Section(lineId, upStationId, distance, downStationId);
        Section downTerminalSection = new Section(lineId, downStationId, 0, -1L);
        sectionDao.save( upTerminalSection );
        sectionDao.save( downTerminalSection );
    }

    public LinkedList<Long> getStationsId(long lineId) {
        List<Section> sections = sectionDao.getSectionsOfLine(lineId)
                .stream()
                .collect(Collectors.toList());
        return sortSections(sections);
    }

    private LinkedList<Long> sortSections(List<Section> sections) {
        LinkedList<Long> linkedList = new LinkedList<>();
        Section currentSection = findSectionByNextId(sections, -1L);
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

    public boolean insertSection(SectionDto sectionDto, Long lineId) {
        SectionType sectionType = matchStation(sectionDto, lineId);
        if( sectionType == SectionType.EXCEPTION ) {
            return false;
        }
        sectionDao.save(sectionType.getNewSection());
        sectionDao.update(sectionType.getPrevSection());
        return true;
    }

    private SectionType matchStation(SectionDto sectionDto, Long lineId) {
        Section upSection = sectionDao.getSection(sectionDto.getUpStationId(), lineId);
        Section downSection = sectionDao.getSection(sectionDto.getDownStationId(), lineId);

        SectionType sectionType = confirmSectionType(upSection, downSection, sectionDto);
        sectionType.updateDistance();

        return sectionType;
    }

    private SectionType confirmSectionType(Section upSection, Section downSection, SectionDto sectionDto ) {
        if( (upSection == null && downSection == null) || (upSection != null && downSection != null) ) {
            return SectionType.EXCEPTION;
        }
        SectionType sectionType = matchSectionType(upSection, downSection, sectionDto);

        if( sectionType.invalidateDistance() ) {
            return SectionType.EXCEPTION;
        }
        return sectionType;
    }

    private SectionType matchSectionType(Section upSection, Section downSection, SectionDto sectionDto) {
        if( upSection != null ) {
            SectionType sectionType = SectionType.INSERT_DOWN_STATION;
            sectionType.setNewSections(new Section(upSection.getLineId(), sectionDto.getDownStationId(), sectionDto.getDistance(), upSection.getNextStationId()));
            sectionType.setPrevSections(upSection);
            return sectionType;
        }
        SectionType sectionType = SectionType.INSERT_UP_STATION;
        sectionType.setNewSections(new Section(downSection.getLineId(), sectionDto.getUpStationId(), sectionDto.getDistance(), downSection.getStationId()));
        sectionType.setPrevSections(sectionDao.getSectionByNextId(downSection.getStationId()));
        return sectionType;
    }

}
