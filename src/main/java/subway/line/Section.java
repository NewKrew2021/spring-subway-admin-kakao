package subway.line;

import java.util.Objects;

public class Section {

    public static Long VIRTUAL_ENDPOINT_ID = -1L;

    private Long id;
    private final Long lineId;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    public Section(Long id, Long lineId, Long upStationId, Long downStationId, int distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException("구간 길이는 0보다 작거나 같은 수 없습니다.");
        }
        if (upStationId.equals(VIRTUAL_ENDPOINT_ID) || downStationId.equals(VIRTUAL_ENDPOINT_ID)) {
            distance = Integer.MAX_VALUE;
        }
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(Long lineId, Long upStationId, Long downStationId, int distance) {
        this(null, lineId, upStationId, downStationId, distance);
    }

    public Section(Long upStationId, Long downStationId, int distance) {
        this(null, null, upStationId, downStationId, distance);
    }
    public boolean shareUpStation(Section counter) {
        return counter.isUpStation(upStationId);
    }

    public boolean isUpStation(Long stationId) {
        return upStationId.equals(stationId);
    }

    public boolean isDownStation(Long stationId) {
        return downStationId.equals(stationId);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return distance == section.distance && Objects.equals(id, section.id) && Objects.equals(lineId, section.lineId) && Objects.equals(upStationId, section.upStationId) && Objects.equals(downStationId, section.downStationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lineId, upStationId, downStationId, distance);
    }
}
