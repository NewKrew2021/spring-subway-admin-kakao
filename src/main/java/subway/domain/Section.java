package subway.domain;

public class Section {

    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public Section() {

    }

    public Section(Long lineId, Long upStationId, Long downStationId, int distance) {
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(Long id, Long lineId, Long upStationId, Long downStationId, int distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public boolean isInsert(Section newSection){
        if(validateDistance(newSection)&&(isMatchedOnlyUpStationId(newSection)||isMatchedOnlyDownStationId(newSection))){
            return true;
        }
        return false;
    }

    private boolean validateDistance(Section newSection){
        return this.distance> newSection.distance;
    }

    private boolean isMatchedOnlyUpStationId(Section newSection){
        if(this.upStationId==newSection.upStationId&&this.downStationId!=newSection.downStationId){
            return true;
        }
        return false;
    }

    private boolean isMatchedOnlyDownStationId(Section newSection){
        if(this.downStationId==newSection.downStationId&&this.upStationId!=newSection.upStationId){
            return true;
        }
        return false;
    }

    public boolean isContainStation(Long stationId){
        if(upStationId.equals(stationId)||downStationId.equals(stationId)){
            return true;
        }
        return false;
    }

    public void mergeSection(Section section){
        if(upStationId.equals(section.downStationId)){
            this.distance+= section.distance;
            this.upStationId= section.upStationId;
        }
        if(this.downStationId.equals(section.upStationId)){
            this.distance+=section.distance;
            this.downStationId= section.downStationId;
        }
    }

    public void modifyMatchedSection(Section newSection) {
        if (isMatchedOnlyUpStationId(newSection)) {
            this.upStationId= newSection.downStationId;;
            this.distance-= newSection.distance;
        }
        if (isMatchedOnlyDownStationId(newSection)) {
            this.downStationId= newSection.upStationId;
            this.distance-= newSection.distance;
        }
    }

    public Long getId() {
        return this.id;
    }

    public Long getLineId() {
        return this.lineId;
    }

    public Long getUpStationId() {
        return this.upStationId;
    }

    public Long getDownStationId() {
        return this.downStationId;
    }

    public int getDistance() {
        return this.distance;
    }


}
