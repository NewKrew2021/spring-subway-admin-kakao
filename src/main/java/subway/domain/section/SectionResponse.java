package subway.domain.section;

public class SectionResponse {
    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public SectionResponse() {

    }

    public SectionResponse(Section section) {
        this.id = section.getId();
        this.lineId = section.getLineId();
        this.upStationId = section.getUpStationId();
        this.downStationId = section.getDownStationId();
        this.distance = section.getDistance();
    }

    public Long getId() {
        return id;
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
}
