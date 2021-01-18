package subway.section;

public class Section {
    private Long upStationId;
    private Long downStationId;
    private Integer distance;
    private Long lineId;
    private Long sectionId;

    public Section() {
    }

    public Section(Long upStationId, Long downStationId, Integer distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(Long upStationId, Long downStationId, Integer distance, Long lineId) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
        this.lineId = lineId;
    }

    public Section(Long sectionId, Long upStationId, Long downStationId, Integer distance, Long lineId) {
        this.sectionId = sectionId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
        this.lineId = lineId;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Integer getDistance() {
        return distance;
    }

    public Long getLineId() {
        return lineId;
    }

    @Override
    public String toString() {
        return "Section{" +
                "upStationId=" + upStationId +
                ", downStationId=" + downStationId +
                ", distance=" + distance +
                ", lineId=" + lineId +
                ", sectionId=" + sectionId +
                '}';
    }

    public Long getSectionId() {
        return sectionId;
    }
}
