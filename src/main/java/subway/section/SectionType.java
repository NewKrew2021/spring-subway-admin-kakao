package subway.section;

public enum SectionType {


    INSERT_DOWN_STATION,
    INSERT_UP_STATION,
    EXCEPTION;

    private Section newSection;
    private Section prevSection;

    public boolean invalidateDistance() {
        if( newSection.getNextStationId() == -1 || prevSection == null ){
            return false;
        }
        return newSection.getDistance() >= prevSection.getDistance();
    }

    public void updateDistance() {
        if( this == INSERT_DOWN_STATION) {
            int distance = newSection.getDistance();
            newSection.setDistance((prevSection.getDistance() == 0) ? 0 :  prevSection.getDistance() - newSection.getDistance());
            prevSection.setDistance(distance);
        }
        if( this == INSERT_UP_STATION && prevSection != null ) {
            prevSection.setDistance(prevSection.getDistance() - newSection.getDistance());
        }
    }

    public void setPrevSections(Section prevSection) {
        this.prevSection = prevSection;
        if( this.prevSection != null ) {
            this.prevSection.setNextStation(this.newSection.getStationId());
        }
    }

    public void setNewSections(Section section) {
        newSection = section;
    }


    public Section getNewSection() {
        return newSection;
    }

    public Section getPrevSection() {
        return prevSection;
    }
}



