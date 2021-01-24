package subway.domain;

import subway.exception.DistanceException;
import subway.exception.IllegalStationException;

import java.util.Objects;

public class Section {
    public static final int ZERO = 0;
    private Long upStationId;
    private Long downStationId;
    private Integer distance;
    private Long lineId;
    private Long sectionId;

    public Section() {
    }

    public Section(Long upStationId, Long downStationId, Integer distance) {
        if (checkProblemStationId(upStationId, downStationId)) {
            throw new IllegalStationException();
        }
        if (distance <= ZERO) {
            throw new DistanceException();
        }
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(Long upStationId, Long downStationId, Integer distance, Long lineId) {
        this(upStationId, downStationId, distance);
        this.lineId = lineId;
    }

    public Section(Long sectionId, Long upStationId, Long downStationId, Integer distance, Long lineId) {
        this(upStationId, downStationId, distance, lineId);
        this.sectionId = sectionId;
    }

    private boolean checkProblemStationId(Long upStationId, Long downStationId) {
        if (upStationId < ZERO) {
            return true;
        }
        if (downStationId < ZERO) {
            return true;
        }
        return upStationId.equals(downStationId);
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Integer getDistance() {
        return distance;
    }

    public Long getLineId() {
        return lineId;
    }

    public Long getSectionId() {
        return sectionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(upStationId, section.upStationId) && Objects.equals(downStationId, section.downStationId) && Objects.equals(lineId, section.lineId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(upStationId, downStationId, lineId);
    }
}
