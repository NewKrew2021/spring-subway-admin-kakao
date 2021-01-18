package subway.line;

import java.util.Objects;

public class Section {
    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public Section() {
    }

    public Section(Long lineId, Long upStationId, Long downStationId, int distance) {
        validateDistance(distance);
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(Long id, Long lineId, Long upStationId, Long downStationId, int distance) {
        validateDistance(distance);
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(Long lineId, SectionRequest sectionRequest){
        validateDistance(sectionRequest.getDistance());
        this.lineId = lineId;
        this.upStationId = sectionRequest.getUpStationId();
        this.downStationId = sectionRequest.getDownStationId();
        this.distance = sectionRequest.getDistance();
    }

    private void validateDistance(int distance) {
        if(distance <= 0) {
            throw new IllegalArgumentException("추가할 구간의 거리는 기존 구간의 거리보다 작아야 합니다.");
        }
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

    public boolean existStation(Section other) {
        return upStationId.equals(other.getUpStationId()) || upStationId.equals(other.getDownStationId())
                || downStationId.equals(other.getDownStationId()) || downStationId.equals(other.getUpStationId());
    }

    public Section getSubSection(Section newSection) {
        if(upStationId == newSection.getUpStationId()) {
            return new Section(lineId, newSection.getDownStationId(), downStationId, distance - newSection.getDistance());
        }
        return new Section(lineId, upStationId, newSection.getUpStationId(), distance - newSection.getDistance());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(lineId, section.lineId)
                && (Objects.equals(upStationId, section.upStationId) && Objects.equals(downStationId, section.downStationId)
                || Objects.equals(upStationId, section.downStationId) && Objects.equals(downStationId, section.upStationId));
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineId, upStationId, downStationId, distance);
    }

}
