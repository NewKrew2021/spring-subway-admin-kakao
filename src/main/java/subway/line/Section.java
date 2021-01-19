package subway.line;

import java.util.Objects;

public class Section {
    public final static int TERMINAL_ID = -1;

    private final long id;
    private final long lineId;
    private final long upStationId;
    private final long downStationId;
    private final int distance;

    public Section(long upStationId, long downStationId, int distance) {
        this(0L, 0L, upStationId, downStationId, distance);
    }

    public Section(long lineId, long upStationId, long downStationId, int distance) {
        this(0L, lineId, upStationId, downStationId, distance);
    }

    public Section(long id, long lineId, long upStationId, long downStationId, int distance) {
        if (isInvalid(upStationId, downStationId)) {
            throw new IllegalArgumentException("Up and down stations cannot be the same");
        }

        if (isNegative(distance)) {
            throw new IllegalArgumentException("Distance cannot be negative");
        }

        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public boolean isUpTerminal() {
        return upStationId == TERMINAL_ID;
    }

    public boolean isDownTerminal() {
        return downStationId == TERMINAL_ID;
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
        return Objects.equals(id, section.id);
    }

    @Override
    public int hashCode() {
        return (int) id;
    }

    private boolean isInvalid(Long upStationId, Long downStationId) {
        return upStationId.equals(downStationId);
    }

    private boolean isNegative(int distance) {
        return distance < 0;
    }
}
