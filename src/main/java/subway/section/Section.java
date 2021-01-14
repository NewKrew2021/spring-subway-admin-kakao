package subway.section;

import java.util.Objects;

public class Section {
    private long lineId;
    private long upStationId;
    private long downStationId;
    private int distance;

    public Section(long lineId, long upStationId, long downStationId, int distance) {
        validate(upStationId, downStationId, distance);
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    private void validate(long upStationId, long downStationId, int distance) {
        if (isInvalidStationId(upStationId, downStationId)) {
            throw new IllegalArgumentException("출발역과 도착역은 같을 수 없습니다.");
        }

        if (isInvalidDistance(distance)) {
            throw new IllegalArgumentException("거리는 0보다 커야 합니다.");
        }
    }

    private boolean isInvalidStationId(long upStationId, long downStationId) {
        return upStationId == downStationId;
    }

    private boolean isInvalidDistance(int distance) {
        return distance <= 0;
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
        return lineId == section.lineId && upStationId == section.upStationId && downStationId == section.downStationId && distance == section.distance;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineId, upStationId, downStationId, distance);
    }
}
