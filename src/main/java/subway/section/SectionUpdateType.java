package subway.section;

public enum SectionUpdateType {

    INSERT_DOWN_SECTION,
    INSERT_UP_SECTION,
    DELETE_SECTION,
    EXCEPTION;

    private Section targetSection;
    private Section prevSection;


    public boolean invalidateDistanceAsInsert() {
        if( targetSection.getNextStationId().equals(Section.WRONG_ID) || prevSection == Section.DO_NOT_EXIST_SECTION ){
            return false;
        }
        return targetSection.getDistance() >= prevSection.getDistance();
    }

    public void updateDistanceAsInsert() {
        if( this == INSERT_DOWN_SECTION) {
            int distance = targetSection.getDistance();
            targetSection.setDistance((prevSection.getDistance() == 0) ? 0 :  prevSection.getDistance() - targetSection.getDistance());
            prevSection.setDistance(distance);
        }
        if( this == INSERT_UP_SECTION && prevSection != Section.DO_NOT_EXIST_SECTION ) {
            prevSection.setDistance(prevSection.getDistance() - targetSection.getDistance());
        }
    }

    public void updatePrevSectionAsInsert() {
        if( this.prevSection != Section.DO_NOT_EXIST_SECTION ) {
            this.prevSection.setNextStation(this.targetSection.getStationId());
        }
    }

    public void updatePrevSectionAsDelete() {
        if( this.prevSection != Section.DO_NOT_EXIST_SECTION ) {
            this.prevSection.setNextStation(this.targetSection.getNextStationId());
        }
    }

    public void setPrevSections(Section prevSection) {
        this.prevSection = prevSection;
    }

    public void setTargetSection(Section newSection) {
        this.targetSection = newSection;
    }

    public Section getTargetSection() {
        return targetSection;
    }

    public Section getPrevSection() {
        return prevSection;
    }

}



