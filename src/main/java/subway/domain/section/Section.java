package subway.domain.section;

import subway.exceptions.IllegalSectionCreateException;
import subway.exceptions.IllegalSectionSubtraction;
import subway.dto.SectionDto;

import java.util.Objects;

public class Section {
    private Long id;
    private Long lineId;
    private Long upStationId, downStationId;
    private int distance;

    public Section() {

    }

    public Section(Long id, Long lineId, Long upStationId, Long downStationId, int distance) {
        validateCreation(upStationId, downStationId, distance);
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(Long lineId, Long upStationId, Long downStationId, int distance) {
        validateCreation(upStationId, downStationId, distance);
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(Long lineId, SectionDto dto) {
        validateCreation(dto.getUpStationId(), dto.getDownStationId(), dto.getDistance());
        this.lineId = lineId;
        this.upStationId = dto.getUpStationId();
        this.downStationId = dto.getDownStationId();
        this.distance = dto.getDistance();
    }

    private static void validateCreation(Long upStationId, Long downStationId, int distance) {
        if(distance <= 0) throw new IllegalSectionCreateException("distance는 0보다 커야 합니다.");
        if(upStationId.equals(downStationId)) throw new IllegalSectionCreateException("upStation 와 downStation는 서로 달라야 합니다.");
    }

    public Section subtractBasedOnUpStation(Section newSection) {
        validateSameUpstation(newSection);
        return new Section(
                id,
                lineId,
                newSection.downStationId,
                downStationId,
                distance - newSection.distance
        );
    }

    public Section subtractBasedOnDownStation(Section newSection){
        validateSameDownStation(newSection);
        return new Section(
                id,
                lineId,
                upStationId,
                newSection.upStationId,
                distance - newSection.distance
        );
    }

    private void validateSameDownStation(Section newSection){
        if(!downStationId.equals(newSection.downStationId)) {
            throw new IllegalSectionSubtraction("downStationId가 같지 않습니다.");
        }
    }

    private void validateSameUpstation(Section newSection){
        if(!upStationId.equals(newSection.upStationId)) {
            throw new IllegalSectionSubtraction("upStationId가 같지 않습니다.");
        }
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

    public Long getLineId() {
        return lineId;
    }

    public int getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Section)) return false;
        Section section = (Section) o;
        return distance == section.distance && Objects.equals(id, section.id) && Objects.equals(lineId, section.lineId) && Objects.equals(upStationId, section.upStationId) && Objects.equals(downStationId, section.downStationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lineId, upStationId, downStationId, distance);
    }
}
