package subway.domain;

import subway.exception.DistanceException;
import subway.exception.IllegalStationException;

import java.util.Objects;

public class Section {
    public static final int ZERO = 0;
    private Station upStation;
    private Station downStation;
    private Integer distance;
    private Long lineId;
    private Long sectionId;

    public Section() {
    }

    public Section(Station upStation, Station downStation, Integer distance) {
        if (upStation.equals(downStation)) {
            throw new IllegalStationException();
        }
        if (distance <= ZERO) {
            throw new DistanceException();
        }
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section(Station upStation, Station downStation, Integer distance, Long lineId) {
        this(upStation, downStation, distance);
        this.lineId = lineId;
    }

    public Section(Long sectionId, Station upStation, Station downStation, Integer distance, Long lineId) {
        this(upStation, downStation, distance, lineId);
        this.sectionId = sectionId;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Integer getDistance() {
        return distance;
    }

    public Long getLineId() {
        return lineId;
    }

    public Long getSectionId() {
        return sectionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(upStation, section.upStation) && Objects.equals(downStation, section.downStation) && Objects.equals(lineId, section.lineId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(upStation, downStation, lineId);
    }
}
