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

    public boolean isMatchedOnlyUpStationId(Section newSection){
        if(this.upStationId==upStationId&&this.downStationId!=downStationId){
            return true;
        }
        return false;
    }

    public boolean isMatchedOnlyDownStationId(Section newSection){
        if(this.downStationId==downStationId&&this.upStationId!=upStationId){
            return true;
        }
        return false;
    }

    public boolean isContainStation(Long stationId){
        System.out.println("비교"+upStationId+" "+downStationId+":"+stationId);
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

    public boolean validateDistance(Section newSection){
        return this.distance> newSection.distance;
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
