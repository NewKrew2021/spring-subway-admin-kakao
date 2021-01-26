package subway.section;

import subway.exception.exceptions.InvalidSectionException;

import java.util.Objects;

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
        validateDistance(distance);
    }

    public void updateDownStationAndDistance(long newDownStationId, int subtrahend) {
        downStationId = newDownStationId;
        distance -= subtrahend;
        validateDistance(distance);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return id == section.id && lineId == section.lineId && upStationId == section.upStationId && downStationId == section.downStationId && distance == section.distance;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lineId, upStationId, downStationId, distance);
    }
}
