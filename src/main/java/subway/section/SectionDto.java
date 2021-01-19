package subway.section;

public class SectionDto {

    private Long upStationId;
    private Long downStationId;
    private int distance;

    public SectionDto(SectionRequest sectionRequest) {
        this.upStationId = sectionRequest.getUpStationId();
        this.downStationId = sectionRequest.getDownStationId();
        this.distance = sectionRequest.getDistance();
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

}
