package subway.section;

import subway.station.Station;

import java.util.Objects;

public class Section {
    private Long lineId;
    private Station upStation;
    private Station downStation;
    private int distance;
    private Long id;

    public Section(Station upStation, Station downStation, int distance) {
        this(null, null, upStation, downStation, distance);
    }

    public Section(Long lineId, Station upStation, Station downStation, int distance) {
       this(null, lineId, upStation, downStation, distance);
    }

    public Section(Long id, Long lineId, Long upStationId, Long downStationId, int distance) {
        this(id, lineId, new Station(upStationId), new Station(downStationId), distance);
    }

    public Section(Long id, Long lineId, Station upStation, Station downStation, int distance) {
        //TODO
        if(upStation.getId().equals(downStation.getId())) {
            throw new IllegalArgumentException();
        }
        if(distance <= 0) {
            throw new IllegalArgumentException();
        }
        this.id = id;
        this.lineId = lineId;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section split(Section section) {
        if(getUpStationId().equals(section.getUpStationId())) {
            return new Section(null, lineId, section.getDownStationId(), getDownStationId(), distance - section.distance);
        }
        return new Section(null, lineId, getUpStationId(), section.getUpStationId(), distance - section.distance);
    }

    public Section attach(Section other) {
        return new Section(null, lineId, upStation, other.downStation, distance + other.distance);
    }

    public Long getId() {
        return id;
    }

    public Long getLineId() {
        return lineId;
    }

    public int getDistance() {
        return distance;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Long getUpStationId() {
        return upStation.getId();
    }

    public Long getDownStationId() {
        return downStation.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return distance == section.distance && Objects.equals(lineId, section.lineId) && Objects.equals(upStation, section.upStation) && Objects.equals(downStation, section.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineId, upStation, downStation, distance, id);
    }
}
