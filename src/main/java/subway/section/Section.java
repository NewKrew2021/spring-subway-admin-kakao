package subway.section;

import subway.exception.exceptions.InvalidSectionException;

public class Section {

    private static final String INVALID_DISTANCE_MESSAGE = "추가될 구간의 거리가 기존 노선 거리보다 깁니다.";

    private long id;
    private long lineId;
    private long upStationId;
    private long downStationId;
    private int distance;

    public Section(long upStationId, long downStationId) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
    }

    public Section(long upStationId, long downStationId, int distance) {
        this(upStationId, downStationId);
        validateDistance(distance);
        this.distance = distance;
    }

    public Section(long lineId, long upStationId, long downStationId, int distance) {
        this(upStationId, downStationId, distance);
        this.lineId = lineId;
    }

    public Section(long id, long lineId, long upStationId, long downStationId, int distance) {
        this(lineId, upStationId, downStationId, distance);
        this.id = id;
    }

    private void validateDistance(int distance) {
        if (distance <= 0) {
            throw new InvalidSectionException(INVALID_DISTANCE_MESSAGE);
        }
    }

    public void updateUpStationAndDistance(long newUpStationId, int subtrahend) {
        upStationId = newUpStationId;
        distance -= subtrahend;
    }

    public void updateDownStationAndDistance(long newDownStationId, int subtrahend) {
        downStationId = newDownStationId;
        distance -= subtrahend;
    }

    public long getId() {
        return id;
    }

    public long getLineId() {
        return lineId;
    }

    public long getUpStationId() {
        return upStationId;
    }

    public long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }
}
