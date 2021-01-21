package subway.domain.section;

import subway.domain.line.LineRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Section {
    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public Section() {
    }

    private Section(Long id, Long lineId, Long upStationId, Long downStationId, int distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    private Section(Long lineId, Long upStationId, Long downStationId, int distance) {
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public static Section of(Long lineId, LineRequest lineRequest) {
        return new Section(lineId, lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance());
    }

    public static Section of(Long lineId, SectionRequest sectionRequest) {
        return new Section(lineId, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());
    }

    public static Section of(Long id, Section section) {
        return new Section(id, section.getLineId(), section.getUpStationId(), section.getDownStationId(), section.getDistance());
    }

    public static Section of(Long id, Long lineId, Long upStationId, Long downStationId, int distance) {
        return new Section(id, lineId, upStationId, downStationId, distance);
    }

    public static Section of(Long lineId, Long upStationId, Long downStationId, int distance) {
        return new Section(lineId, upStationId, downStationId, distance);
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

    public boolean isSameSection(Section newSection) {
        boolean same1 = upStationId.equals(newSection.getUpStationId()) && downStationId.equals(newSection.getDownStationId());
        boolean same2 = upStationId.equals(newSection.getDownStationId()) && downStationId.equals(newSection.getUpStationId());
        return same1 || same2;
    }

    public boolean containStation(Section newSection) {
        List<Long> stationIds = Arrays.asList(newSection.getUpStationId(), newSection.getDownStationId());
        return stationIds.contains(upStationId) || stationIds.contains(downStationId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return distance == section.distance && Objects.equals(id, section.id) && Objects.equals(lineId, section.lineId)
                && Objects.equals(upStationId, section.upStationId) && Objects.equals(downStationId, section.downStationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lineId, upStationId, downStationId, distance);
    }
}
