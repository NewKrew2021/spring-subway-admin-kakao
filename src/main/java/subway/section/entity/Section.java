package subway.section.entity;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Section {
    private Long id;
    private final long lineId;
    private final long upStationId;
    private final long downStationId;
    private final int distance;

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

    public Section getCollapsedSection(Section insertSection) {
        assert isCollapsible(insertSection);
        if (hasSameUpStation(insertSection)) {
            return new Section(
                    id,
                    lineId,
                    insertSection.downStationId,
                    downStationId,
                    distance - insertSection.distance
            );
        }

        assert hasSameDownStation(insertSection);
        return new Section(
                id,
                lineId,
                upStationId,
                insertSection.upStationId,
                distance - insertSection.distance
        );
    }

    public boolean isCollapsible(Section insertSection) {
        return (hasSameUpStation(insertSection) != hasSameDownStation(insertSection))
                && distance > insertSection.distance;
    }

    private boolean hasSameUpStation(Section section) {
        return upStationId == section.upStationId;
    }

    private boolean hasSameDownStation(Section section) {
        return downStationId == section.downStationId;
    }

    public boolean containsStation(Long stationId) {
        return stationId != null && getStationIds().contains(stationId);
    }

    public List<Long> getStationIds() {
        return Arrays.asList(upStationId, downStationId);
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
