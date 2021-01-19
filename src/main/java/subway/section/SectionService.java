package subway.section;

import org.springframework.stereotype.Service;
import subway.exception.*;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public boolean insertSection(SectionDto sectionDto, Long lineId) {
        SectionUpdateType sectionUpdateType = updateSectionOfLine(sectionDto, lineId);
        if( sectionUpdateType == SectionUpdateType.EXCEPTION ) {
            return false;
        }
        sectionUpdateType.updateDistanceAsInsert();
        sectionDao.save(sectionUpdateType.getTargetSection());
        sectionDao.update(sectionUpdateType.getPrevSection());
        return true;
    }

    private SectionUpdateType updateSectionOfLine(SectionDto sectionDto, Long lineId) {
        Section upSection = sectionDao.getSection(sectionDto.getUpStationId(), lineId);
        Section downSection = sectionDao.getSection(sectionDto.getDownStationId(), lineId);

        try {
            SectionUpdateType sectionUpdateType = decideSectionTypeAndThrowException(upSection, downSection, sectionDto);
            return sectionUpdateType;
        } catch (Exception e) {
             e.printStackTrace();
        }

        return SectionUpdateType.EXCEPTION;
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

    public boolean deleteSection(long lineId, long stationId) {

        SectionUpdateType sectionUpdateType = deleteValidation(lineId, stationId);

        if( sectionUpdateType == SectionUpdateType.EXCEPTION ) {
            return false;
        }
        sectionUpdateType.updatePrevSectionAsDelete();
        sectionDao.delete(sectionUpdateType.getTargetSection());
        sectionDao.update(sectionUpdateType.getPrevSection());
        return true;

    }

    private SectionUpdateType deleteValidation(long lineId, long stationId) {
        Section section = sectionDao.getSection(stationId,lineId);

        try {
            throwExceptionAsDelete(section, lineId);
        } catch (Exception e) {
            e.printStackTrace();
            return SectionUpdateType.EXCEPTION;
        }

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
