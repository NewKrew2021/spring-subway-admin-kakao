package subway.domain.section;

import java.util.Objects;

public class Section {
    private Long id;
    private Long stationId;
    private Integer distance;
    private Long lineId;

    public Section(Long id, Long stationId, int distance, Long lineId) {
        this.id = id;
        this.stationId = stationId;
        this.distance = distance;
        this.lineId = lineId;
    }

    public Section(Long stationId, int distance, Long lineId) {
        this(null, stationId, distance, lineId);
    }

    public Long getId() {
        return id;
    }

    public Long getStationId() {
        return stationId;
    }

    public int getDistance() {
        return distance;
    }

    public Long getLineId() {
        return lineId;
    }

    public int calculateDistance(Section section) {
        return Math.abs(this.distance - section.distance);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(id, section.id) && Objects.equals(stationId, section.stationId) && Objects.equals(distance, section.distance) && Objects.equals(lineId, section.lineId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, stationId, distance, lineId);
    }
}
