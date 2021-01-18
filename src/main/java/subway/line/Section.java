package subway.line;

import java.util.Objects;

public class Section {

    public static final int VIRTUAL_DISTANCE = 0;

    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public Section() {
    }

    public Section(Long lineId, Long upStationId, Long downStationId, int distance) {
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(Long id, Long lineId, Long upStationId, Long downStationId, int distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(Long lineId, SectionRequest sectionRequest) {
        this.lineId = lineId;
        this.upStationId = sectionRequest.getUpStationId();
        this.downStationId = sectionRequest.getDownStationId();
        this.distance = sectionRequest.getDistance();
    }

    public boolean isUpStation(Long stationId) {
        return getUpStationId() == stationId;
    }

    public boolean isDownStation(Long stationId) {
        return getDownStationId() == stationId;
    }

    public boolean isHeadSection() {
        return getUpStationId() == Line.HEAD;
    }

    public boolean isTailSection() {
        return getDownStationId() == Line.TAIL;
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(lineId, section.lineId) && (Objects.equals(upStationId, section.upStationId) && Objects.equals(downStationId, section.downStationId)
                || Objects.equals(upStationId, section.downStationId) && Objects.equals(downStationId, section.upStationId));
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineId, upStationId, downStationId, distance);
    }
}
