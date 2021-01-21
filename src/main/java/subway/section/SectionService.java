package subway.section;

import org.springframework.stereotype.Service;
import subway.exception.*;
import subway.line.Line;

import java.util.LinkedList;
import java.util.List;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public void createTerminalSections(Line line, Long lineId) {
        Section upTerminalSection = new Section(lineId, line.getUpStationId(), line.getDistance(), line.getDownStationId());
        Section downTerminalSection = new Section(lineId, line.getDownStationId(), 0, Section.WRONG_ID);
        sectionDao.save( upTerminalSection );
        sectionDao.save( downTerminalSection );
    }

    public LinkedList<Long> getStationsIdOfLine(long lineId) {
        Sections sections = new Sections(sectionDao.getSectionsOfLine(lineId));
        return sections.getSortingStationId();
    }

    public void insertSection(SectionDto sectionDto) {
        SectionUpdateType sectionUpdateType = decideSectionTypeAndThrowException(sectionDto);
        sectionUpdateType.updateDistanceAsInsert(sectionDto);
        sectionDao.save(sectionDto.getTargetSection());
        sectionDao.update(sectionDto.getPrevSection());
    }

    private SectionUpdateType decideSectionTypeAndThrowException(SectionDto sectionDto ) {
        Section upSection = sectionDao.getSection(sectionDto.getUpStationId(), sectionDto.getLineId());
        Section downSection = sectionDao.getSection(sectionDto.getDownStationId(), sectionDto.getLineId());

        areExistSectionOnlyOne(upSection, downSection);
        SectionUpdateType sectionUpdateType = confirmSectionUpOrDown(upSection, downSection, sectionDto);
        sectionDto.getPrevSection().updateNextSectionToOtherStation(sectionDto.getTargetSection());

        return sectionUpdateType;
    }

    private void areExistSectionOnlyOne(Section upSection, Section downSection) {
        if( !upSection.isExist() && !downSection.isExist() ) {
            throw new NotExistSectionInsertException();
        }
        if( upSection.isExist() && downSection.isExist() ) {
            throw new BothExistSectionException();
        }
    }

    private SectionUpdateType confirmSectionUpOrDown(Section upSection, Section downSection, SectionDto sectionDto) {
        if( upSection.isExist() ) {
            SectionUpdateType sectionUpdateType = SectionUpdateType.INSERT_DOWN_SECTION;
            sectionDto.setTargetSection(new Section(upSection.getLineId(), sectionDto.getDownStationId(), sectionDto.getDistance(), upSection.getNextStationId()));
            sectionDto.setPrevSections(upSection);
            return sectionUpdateType;
        }
        SectionUpdateType sectionUpdateType = SectionUpdateType.INSERT_UP_SECTION;
        sectionDto.setTargetSection(new Section(downSection.getLineId(), sectionDto.getUpStationId(), sectionDto.getDistance(), downSection.getStationId()));
        sectionDto.setPrevSections(sectionDao.getSectionByNextId(downSection.getStationId()));
        return sectionUpdateType;
    }

    public void deleteSection(SectionDto sectionDto) {
        deleteValidation(sectionDto);
        sectionDto.getPrevSection().updateNextStationToOtherNextStation(sectionDto.getTargetSection());
        sectionDao.delete(sectionDto.getTargetSection());
        sectionDao.update(sectionDto.getPrevSection());
    }

    private void deleteValidation(SectionDto sectionDto) {
        Section section = sectionDao.getSection(sectionDto);

        if(section.isPossibleDelete(sectionDao.countOfSections(sectionDto.getLineId()))) {
            Section prevSection = sectionDao.getSectionByNextId(sectionDto.getStationId());
            sectionDto.setTargetSection(section);
            sectionDto.setPrevSections(prevSection);
        }
    }
}
