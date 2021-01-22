package subway.section;

public class SectionDto {

    private Long upStationId;
    private Long downStationId;
    private int distance;
    private Long lineId;
    private Long stationId;

    private Section targetSection;
    private Section prevSection;

    public SectionDto(Long lineId, Long stationId) {
        this.lineId = lineId;
        this.stationId = stationId;
    }

    public SectionDto(SectionRequest sectionRequest, Long lineId) {
        this.upStationId = sectionRequest.getUpStationId();
        this.downStationId = sectionRequest.getDownStationId();
        this.distance = sectionRequest.getDistance();
        this.lineId = lineId;
    }

    public Long getUpStationId() {
        return upStationId;
    }
    public Long getDownStationId() {
        return downStationId;
    }
    public int getDistance() {
        return distance;
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

    public Long getLineId() {
        return lineId;
    }

    public void setLineId(Long lineId) {
        this.lineId = lineId;
    }

    public Long getStationId() {
        return stationId;
    }

    public void setStationId(Long stationId) {
        this.stationId = stationId;
    }
}
