package subway.section;

import java.util.Objects;

public class Section {
    static final long TERMINAL_ID = -1;
    static final int INF = Integer.MAX_VALUE;
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

    public Section getDifferenceSection(Section section) {
        if (section.distance >= distance) {
            throw new IllegalStateException("구간의 거리는 현재 구간의 거리보다 더 짧아야 합니다.");
        }

        if ((section.upStationId == upStationId)
                == (section.downStationId == downStationId)) {
            throw new IllegalStateException("상/하행역 중 단 하나만 일치해야 합니다.");
        }

        if (section.upStationId == upStationId) {
            return new Section(lineId,
                    section.downStationId,
                    downStationId,
                    distance - section.distance);
        }
        return new Section(lineId,
                upStationId,
                section.upStationId,
                distance - section.distance);
    }

    public boolean hasSameUpStation(Section newSection) {
        return upStationId == newSection.upStationId;
    }

    public boolean hasSameDownStation(Section newSection) {
        return downStationId == newSection.downStationId;
    }

    public boolean isUpTerminal() {
        return upStationId == TERMINAL_ID;
    }

    public boolean isDownTerminal() {
        return downStationId == TERMINAL_ID;
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
