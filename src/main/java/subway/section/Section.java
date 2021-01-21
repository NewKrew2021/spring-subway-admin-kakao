package subway.section;

import subway.distance.Distance;
import subway.line.Line;

import java.util.Objects;

public class Section {

    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private Distance distance;

    public Section() {
    }

    public Section(Long lineId, Long upStationId, Long downStationId, int distance) {
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = new Distance(distance);
    }

    public Section(Long id, Long lineId, Long upStationId, Long downStationId, int distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = new Distance(distance);
    }

    public Section(Long lineId, SectionRequest sectionRequest) {
        this.lineId = lineId;
        this.upStationId = sectionRequest.getUpStationId();
        this.downStationId = sectionRequest.getDownStationId();
        this.distance = new Distance(sectionRequest.getDistance());
    }

    public Section(Long id, Long lineId, Long upStationId, Long downStationId, Distance distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(Long lineId, Long upStationId, Long downStationId, Distance distance) {
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public boolean isUpStation(Long stationId) {
        return getUpStationId() == stationId;
    }

    public boolean isDownStation(Long stationId) {
        return getDownStationId() == stationId;
    }

    public boolean isExist(Section another) {
        if (upStationId.equals(another.upStationId) && distance.isExist(another.distance)) {
            return true;
        }
        if (downStationId.equals(another.downStationId) && distance.isExist(another.distance)) {
            return true;
        }
        return false;
    }

    public boolean isHeadSection() {
        return getUpStationId() == Line.HEADID;
    }

    public Distance sumDistance(Section another) {
        return this.distance.sumDistance(another.getDistance());
    }

    public Distance subtractDistance(SectionRequest another) {
        return this.distance.subtractDistance(another.getDistance());
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

    public Distance getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(lineId, section.lineId) &&
                (upStationId.equals(section.upStationId) && downStationId.equals(section.downStationId)) ||
                (upStationId.equals(section.upStationId) && downStationId.equals(section.upStationId));

    }

    @Override
    public int hashCode() {
        return Objects.hash(lineId, upStationId, downStationId, distance);
    }
}
