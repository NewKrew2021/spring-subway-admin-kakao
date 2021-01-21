package subway.section.dto;

import java.beans.ConstructorProperties;

public class SectionRequest {
    private Long upStationID;
    private Long downStationID;
    private int distance;

    // TODO: ConstructorProperties를 parameterNamesModule 로 바꿔보기?
    //       or 기본 생성자 생성 후 private?
    @ConstructorProperties({"upStationID", "downStationID", "distance"})
    public SectionRequest(Long upStationID, Long downStationID, int distance) {
        this.upStationID = upStationID;
        this.downStationID = downStationID;
        this.distance = distance;
    }

    public Long getUpStationID() {
        return upStationID;
    }

    public Long getDownStationID() {
        return downStationID;
    }

    public int getDistance() {
        return distance;
    }
}
