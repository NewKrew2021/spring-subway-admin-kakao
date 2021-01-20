package subway.section.vo;

import java.util.Arrays;
import java.util.List;

public class Section {
    private final long lineId;
    private final long upStationId;
    private final long downStationId;
    private final int distance;
    private Long id;

    public Section(long lineId, long upStationId, long downStationId, int distance) {
        validate(upStationId, downStationId, distance);
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(Long id, long lineId, long upStationId, long downStationId, int distance) {
        validate(upStationId, downStationId, distance);
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(Long id, Section section) {
        this(id,
                section.getLineId(),
                section.getUpStationId(),
                section.getDownStationId(),
                section.getDistance()
        );
    }

    private void validate(long upStationId, long downStationId, int distance) {
        if (isInvalidStationId(upStationId, downStationId)) {
            throw new IllegalArgumentException("출발역과 도착역은 같을 수 없습니다.");
        }

        if (isInvalidDistance(distance)) {
            throw new IllegalArgumentException("구간의 거리는 0보다 커야 합니다.");
        }
    }

    private boolean isInvalidStationId(long upStationId, long downStationId) {
        return upStationId == downStationId;
    }

    private boolean isInvalidDistance(int distance) {
        return distance <= 0;
    }

    private boolean hasSameUpStation(Section section) {
        return upStationId == section.upStationId;
    }

    private boolean hasSameDownStation(Section section) {
        return downStationId == section.downStationId;
    }

    public boolean containsStation(Long stationId) {
        return stationId != null && (stationId.equals(upStationId) || stationId.equals(downStationId));
    }

    public boolean isCollapsible(Section section) {
        return (hasSameUpStation(section) != hasSameDownStation(section))
                && distance > section.distance;
    }

    public Section collapse(Section section) {
        assert isCollapsible(section);
        if (hasSameUpStation(section)) {
            return new Section(
                    id,
                    lineId,
                    section.downStationId,
                    downStationId,
                    distance - section.distance
            );
        }

        assert hasSameDownStation(section);
        return new Section(
                id,
                lineId,
                upStationId,
                section.upStationId,
                distance - section.distance
        );
    }

    public Long getId() {
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

    public List<Long> getStationIds() {
        return Arrays.asList(upStationId, downStationId);
    }
}
