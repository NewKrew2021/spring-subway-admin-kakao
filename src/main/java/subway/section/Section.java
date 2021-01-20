package subway.section;

import subway.exception.InvalidSectionException;

public class Section {
    private Long id;
    private Long upStationId;
    private Long downStationId;
    private Integer distance;
    private Long lineId;

    public Section(Long id, Long upStationId, Long downStationId, int distance, Long lineId) {
        validateDistance(distance);
        this.id = id;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
        this.lineId = lineId;
    }

    public Section(Long upStationId, Long downStationId, int distance, Long lineId) {
        this(0L, upStationId, downStationId, distance, lineId);
    }

    public static Section fromRequest(SectionRequest sectionRequest, long lineId) {
        return new Section(sectionRequest.getUpStationId(),
                sectionRequest.getDownStationId(),
                sectionRequest.getDistance(),
                lineId);
    }

    public Long getId() {
        return id;
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

    public Long getLineId() {
        return lineId;
    }

    public Section getSectionDownStationChanged(Section newSection) {
        return new Section(getId(), getUpStationId(), newSection.upStationId, getDistance() - newSection.distance, getLineId());
    }

    public Section getSectionUpStationChanged(Section newSection) {
        return new Section(getId(), newSection.downStationId, getDownStationId(), getDistance() - newSection.distance, getLineId());
    }

    public Section getMergedSection(Section otherSection) {
        return new Section(getId(), getUpStationId(), otherSection.downStationId, getDistance() + otherSection.distance, getLineId());
    }

    private void validateDistance(int distance) {
        if (distance <= 0) {
            throw new InvalidSectionException("구간의 길이는 0보다 커야 합니다.");
        }
    }
}
