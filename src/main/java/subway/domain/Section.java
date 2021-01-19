package subway.domain;

import subway.exception.custom.DifferentLineIdException;
import subway.exception.custom.IllegalDistanceException;

import java.util.Objects;

public class Section {
    private final Long lineId;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;
    private final Long id;

    public Section(Long id, Long lineId, Long upStationId, Long downStationId, int distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(Long lineId, Long upStationId, Long downStationId, int distance) {
        this(null, lineId, upStationId, downStationId, distance);
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

    public Section getAnotherSection(Section sectionToAdd) {
        validateLineId(sectionToAdd);
        validateDistance(sectionToAdd);
        // 하행 기준 분리 ex, (A - C) 에 (B - C) 추가
        if (isDownMatchPortion(sectionToAdd)) {
            return new Section(lineId, upStationId, sectionToAdd.getUpStationId(),
                    distance - sectionToAdd.getDistance());
        }
        // 상행 기준 분리 ex, (A - C) 에 (A - B) 추가
        if (isUpMatchPortion(sectionToAdd)) {
            return new Section(lineId, sectionToAdd.getDownStationId(), downStationId,
                    distance - sectionToAdd.getDistance());
        }
        throw new IllegalArgumentException("분리할 수 없는 구간입니다.");
    }

    public Section mergeSection(Section sectionToMerge) {
        validateLineId(sectionToMerge);
        if (canMergeOnDownStation(sectionToMerge)) {
            return new Section(lineId, upStationId, sectionToMerge.getDownStationId(),
                    sectionToMerge.getDistance() + distance);
        }
        if (canMergeOnUpStation(sectionToMerge)) {
            return new Section(lineId, sectionToMerge.getUpStationId(), downStationId,
                    sectionToMerge.getDistance() + distance);
        }
        throw new IllegalArgumentException("합칠 수 없는 구간입니다.");
    }

    private void validateLineId(Section sectionToMerge) {
        if (!lineId.equals(sectionToMerge.lineId)) {
            throw new DifferentLineIdException();
        }
    }

    private void validateDistance(Section sectionToAdd) {
        if (distance <= sectionToAdd.getDistance()) {
            throw new IllegalDistanceException();
        }
    }

    private boolean isDownMatchPortion(Section sectionToAdd) {
        return downStationId.equals(sectionToAdd.getDownStationId()) &&
                !upStationId.equals(sectionToAdd.getUpStationId());
    }

    private boolean isUpMatchPortion(Section sectionToAdd) {
        return !downStationId.equals(sectionToAdd.getDownStationId()) &&
                upStationId.equals(sectionToAdd.getUpStationId());
    }

    private boolean canMergeOnDownStation(Section sectionToMerge) {
        return downStationId.equals(sectionToMerge.getUpStationId());
    }

    private boolean canMergeOnUpStation(Section sectionToMerge) {
        return upStationId.equals(sectionToMerge.getDownStationId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return distance == section.distance && Objects.equals(lineId, section.lineId) &&
                Objects.equals(upStationId, section.upStationId) &&
                Objects.equals(downStationId, section.downStationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineId, upStationId, downStationId, distance);
    }
}
