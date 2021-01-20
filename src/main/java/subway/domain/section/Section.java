package subway.domain.section;

import subway.exception.SectionCreateException;
import subway.exception.SectionOperationException;
import subway.domain.station.Station;

import java.util.Objects;

public class Section {
    private final Long lineId;
    private final Station upStation;
    private final Station downStation;
    private final int distance;
    private final Long id;

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
        checkStations(upStation, downStation);
        checkDistance(distance);
        this.id = id;
        this.lineId = lineId;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section split(Section section) {
        if(!getUpStationId().equals(section.getUpStationId()) && !getDownStationId().equals(section.getDownStationId())) {
            throw new SectionOperationException(SectionOperationException.SECTION_SPLIT_ERROR);
        }
        if(getUpStationId().equals(section.getUpStationId())) {
            return new Section(null, lineId, section.downStation, downStation, distance - section.distance);
        }
        return new Section(null, lineId, upStation, section.upStation, distance - section.distance);
    }

    public Section attach(Section other) {
        if(!downStation.getId().equals(other.getUpStationId())) {
            throw new SectionOperationException(SectionOperationException.SECTION_ATTACH_ERROR);
        }
        return new Section(null, lineId, upStation, other.downStation, distance + other.distance);
    }

    private void checkStations(Station upStation, Station downStation) {
        if(upStation.getId().equals(downStation.getId())) {
            throw new SectionCreateException(SectionCreateException.DUPLICATE_STATION_EXCEPTION);
        }
    }

    private void checkDistance(int distance) {
        if(distance <= 0) {
            throw new SectionCreateException(SectionCreateException.DISTANCE_EXCEPTION + distance);
        }
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

    @Override
    public String toString() {
        return "Section{" +
                "lineId=" + lineId +
                ", upStation=" + upStation +
                ", downStation=" + downStation +
                ", distance=" + distance +
                ", id=" + id +
                '}';
    }
}
