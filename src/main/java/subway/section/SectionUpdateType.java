package subway.section;

public enum SectionUpdateType {

    INSERT_DOWN_SECTION,
    INSERT_UP_SECTION,
    DELETE_SECTION,
    EXCEPTION;

    private Section targetSection;
    private Section prevSection;

    public boolean invalidateDistanceAsInsert() {
        if( targetSection.getNextStationId() == -1 || prevSection == null ){
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
        if( this == INSERT_UP_SECTION && prevSection != null ) {
            prevSection.setDistance(prevSection.getDistance() - targetSection.getDistance());
        }
    }

    public void updatePrevSectionAsInsert() {
        if( this.prevSection != null ) {
            this.prevSection.setNextStation(this.targetSection.getStationId());
        }
    }

    public void updatePrevSectionAsDelete() {
        if( this.prevSection != null ) {
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



