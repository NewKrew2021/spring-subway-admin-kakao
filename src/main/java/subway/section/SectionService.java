package subway.section;

import org.springframework.stereotype.Service;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public boolean insertSection(SectionDto sectionDto, Long lineId) {
        SectionUpdateType sectionUpdateType = matchStation(sectionDto, lineId);
        if( sectionUpdateType == SectionUpdateType.EXCEPTION ) {
            return false;
        }
        sectionDao.save(sectionUpdateType.getTargetSection());
        sectionDao.update(sectionUpdateType.getPrevSection());
        return true;
    }

    private SectionUpdateType matchStation(SectionDto sectionDto, Long lineId) {
        Section upSection = sectionDao.getSection(sectionDto.getUpStationId(), lineId);
        Section downSection = sectionDao.getSection(sectionDto.getDownStationId(), lineId);

        SectionUpdateType sectionUpdateType = confirmSectionType(upSection, downSection, sectionDto);
        sectionUpdateType.updateDistanceAsInsert();

        return sectionUpdateType;
    }

    private SectionUpdateType confirmSectionType(Section upSection, Section downSection, SectionDto sectionDto ) {
        if( (upSection == null && downSection == null) || (upSection != null && downSection != null) ) {
            return SectionUpdateType.EXCEPTION;
        }
        SectionUpdateType sectionUpdateType = matchSectionType(upSection, downSection, sectionDto);
        sectionUpdateType.updatePrevSectionAsInsert();

        if( sectionUpdateType.invalidateDistanceAsInsert() ) {
            return SectionUpdateType.EXCEPTION;
        }
        return sectionUpdateType;
    }

    private SectionUpdateType matchSectionType(Section upSection, Section downSection, SectionDto sectionDto) {
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
        if ( section == null || sectionDao.countOfSections(lineId) <= 2) {
            return SectionUpdateType.EXCEPTION;
        }
        Section prevSection = sectionDao.getSectionByNextId(stationId);
        SectionUpdateType sectionUpdateType = SectionUpdateType.DELETE_SECTION;
        sectionUpdateType.setTargetSection(section);
        sectionUpdateType.setPrevSections(prevSection);
        return sectionUpdateType;
    }
}
