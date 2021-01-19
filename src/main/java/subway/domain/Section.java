package subway.domain;

import subway.request.LineRequest;
import subway.request.SectionRequest;

import java.util.Arrays;
import java.util.List;

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
}
