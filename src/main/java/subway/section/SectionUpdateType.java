package subway.section;

import subway.exception.TooLongDistanceSectionException;

public enum SectionUpdateType {

    INSERT_DOWN_SECTION,
    INSERT_UP_SECTION;

    public void updateDistanceAsInsert(SectionDto sectionDto) {
        Section targetSection = sectionDto.getTargetSection();
        Section prevSection = sectionDto.getPrevSection();

        if( this.invalidateDistanceAsInsert(targetSection, prevSection) ) {
            throw new TooLongDistanceSectionException();
        }

        if( this == INSERT_DOWN_SECTION) {
            int distance = targetSection.getDistance();
            targetSection.setDistance((prevSection.getDistance() == 0) ? 0 :  prevSection.getDistance() - targetSection.getDistance());
            prevSection.setDistance(distance);
        }

        if( this == INSERT_UP_SECTION && prevSection != Section.DO_NOT_EXIST_SECTION ) {
            prevSection.setDistance(prevSection.getDistance() - targetSection.getDistance());
        }
    }

    public boolean invalidateDistanceAsInsert(Section targetSection, Section prevSection) {
        if( targetSection.getNextStationId().equals(Section.WRONG_ID) || prevSection == Section.DO_NOT_EXIST_SECTION ){
            return false;
        }
        return targetSection.getDistance() >= prevSection.getDistance();
    }

}



