package subway.line;

public class SectionRequest {
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public SectionRequest() {
    }

    public SectionRequest(Long upStationId, Long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
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

    public SectionType containId(SectionRequest sectionRequest) {
        boolean matchUpStation = this.upStationId.equals(sectionRequest.getUpStationId());
        boolean matchDownStation = this.downStationId.equals(sectionRequest.getDownStationId());

        if(matchDownStation == matchUpStation || this.distance <= sectionRequest.getDistance()) {
            return SectionType.EXCEPTION; // 거리체크
        }
        if( matchUpStation ) {
            return SectionType.UP_STATION;
        }
        return SectionType.DOWN_STATION;
    }

}
