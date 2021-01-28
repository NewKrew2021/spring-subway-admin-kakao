package subway.section.domain;

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

    public Long getLineId() {
        return lineId;
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

    public Long getId() {
        return id;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public void setUpStationId(Long downStationId) {
        this.upStationId = downStationId;
    }

    public void setDownStationId(Long downStationId) {
        this.downStationId = downStationId;
    }

    public void splitBy(Section newSection){
        if (upStationId == newSection.getUpStationId()) {
            setUpStationId(newSection.getDownStationId());
            setDistance(distance - newSection.getDistance());
        }
        if (downStationId == newSection.getDownStationId()) {
            setDownStationId(newSection.getUpStationId());
            setDistance(distance - newSection.getDistance());
        }
    }

    public boolean isIncludeAndOverDistance(Section section){
        return isInclude(section) && isOverDistance(section);
    }

    public boolean isInclude(Section section){
        return section.getUpStationId() == upStationId ||
                section.getDownStationId() == downStationId;
    }

    private boolean isOverDistance(Section section){
        return section.getDistance() >= distance;
    }
}
