package subway.line;

import subway.exceptions.InvalidSectionException;

public class Section {

    private static final String INVALID_DISTANCE_MESSAGE = "추가될 구간의 거리가 기존 노선 거리보다 깁니다.";

    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public Section(Long upStationId, Long downStationId) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
    }

    public Section(Long upStationId, Long downStationId, int distance) {
        this(upStationId, downStationId);
        validateDistance(distance);
        this.distance = distance;
    }

    public Section(Long lineId, Long upStationId, Long downStationId, int distance) {
        this(upStationId, downStationId, distance);
        this.lineId = lineId;
    }

    public Section(Long id, Long lineId, Long upStationId, Long downStationId, int distance) {
        this(lineId, upStationId, downStationId, distance);
        this.id = id;
    }

    private void validateDistance(int distance) {
        if (distance < 0) {
            throw new InvalidSectionException(INVALID_DISTANCE_MESSAGE);
        }
    }

    public Long getId() {
        return id;
    }

    public Long getLineId() { return lineId; }

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
