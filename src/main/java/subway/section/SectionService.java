package subway.section;

import org.springframework.stereotype.Service;
import subway.exception.*;
import subway.line.LineDto;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public void createTerminalSections(LineDto lineDto, Long lineId) {
        Section upTerminalSection = new Section(lineId, lineDto.getUpStationId(), lineDto.getDistance(), lineDto.getDownStationId());
        Section downTerminalSection = new Section(lineId, lineDto.getDownStationId(), 0, Section.WRONG_ID);
        sectionDao.save( upTerminalSection );
        sectionDao.save( downTerminalSection );
    }

    public LinkedList<Long> getStationsIdOfLine(long lineId) {
        List<Section> sections = sectionDao.getSectionsOfLine(lineId);
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

    public void insertSection(SectionDto sectionDto, Long lineId) {
        SectionUpdateType sectionUpdateType = updateSectionOfLine(sectionDto, lineId);
        sectionUpdateType.updateDistanceAsInsert();
        sectionDao.save(sectionUpdateType.getTargetSection());
        sectionDao.update(sectionUpdateType.getPrevSection());
    }

    private SectionUpdateType updateSectionOfLine(SectionDto sectionDto, Long lineId) {
        Section upSection = sectionDao.getSection(sectionDto.getUpStationId(), lineId);
        Section downSection = sectionDao.getSection(sectionDto.getDownStationId(), lineId);

        SectionUpdateType sectionUpdateType = decideSectionTypeAndThrowException(upSection, downSection, sectionDto);
        return sectionUpdateType;
    }

    private SectionUpdateType decideSectionTypeAndThrowException(Section upSection, Section downSection, SectionDto sectionDto ) {
        if( upSection == Section.DO_NOT_EXIST_SECTION && downSection == Section.DO_NOT_EXIST_SECTION ) {
            throw new NotExistSectionInsertException();
        }
        if( upSection != Section.DO_NOT_EXIST_SECTION && downSection != Section.DO_NOT_EXIST_SECTION ) {
            throw new BothExistSectionException();
        }

        SectionUpdateType sectionUpdateType = confirmSectionUpOrDown(upSection, downSection, sectionDto);
        sectionUpdateType.updatePrevSectionAsInsert();

        if( sectionUpdateType.invalidateDistanceAsInsert() ) {
            throw new TooLongDistanceSectionException();
        }
        return sectionUpdateType;
    }

    private SectionUpdateType confirmSectionUpOrDown(Section upSection, Section downSection, SectionDto sectionDto) {
        if( upSection != null ) {
            SectionUpdateType sectionUpdateType = SectionUpdateType.INSERT_DOWN_SECTION;
            sectionUpdateType.setTargetSection(new Section(upSection.getLineId(), sectionDto.getDownStationId(), sectionDto.getDistance(), upSection.getNextStationId()));
            sectionUpdateType.setPrevSections(upSection);
            return sectionUpdateType;
        }
        SectionUpdateType sectionUpdateType = SectionUpdateType.INSERT_UP_SECTION;
        sectionUpdateType.setTargetSection(new Section(downSection.getLineId(), sectionDto.getUpStationId(), sectionDto.getDistance(), downSection.getStationId()));
        sectionUpdateType.setPrevSections(sectionDao.getSectionByNextId(downSection.getStationId()));
        return sectionUpdateType;
    }

    public void deleteSection(long lineId, long stationId) {
        SectionUpdateType sectionUpdateType = deleteValidation(lineId, stationId);
        sectionUpdateType.updatePrevSectionAsDelete();
        sectionDao.delete(sectionUpdateType.getTargetSection());
        sectionDao.update(sectionUpdateType.getPrevSection());
    }

    private SectionUpdateType deleteValidation(long lineId, long stationId) {
        Section section = sectionDao.getSection(stationId,lineId);
        throwExceptionAsDelete(section, lineId);
        Section prevSection = sectionDao.getSectionByNextId(stationId);
        SectionUpdateType sectionUpdateType = SectionUpdateType.DELETE_SECTION;
        sectionUpdateType.setTargetSection(section);
        sectionUpdateType.setPrevSections(prevSection);
        return sectionUpdateType;
    }

    private void throwExceptionAsDelete(Section section, Long lineId) {
        if ( section == null ) {
            throw new NotExistSectionDeleteException();
        }
        if ( sectionDao.countOfSections(lineId) <= 2) {
            throw new TooFewSectionAsDeleteException();
        }
    }
}
