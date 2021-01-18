package subway.line;

public class SectionResponse {
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public SectionResponse() {
    }

    public SectionResponse(Long upStationId, Long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public SectionResponse(Section section) {
        this(section.getUpStationId(),
                section.getDownStationId(),
                section.getDistance());
    }

    public int getDistance() {
        return distance;
    }
}
