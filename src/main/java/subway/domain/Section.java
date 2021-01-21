package subway.domain;

import java.util.Objects;

public class Section {
    private Long id;
    private Line line;
    private Station upStation;
    private Station downStation;
    private int distance;

    public Section() {
    }

    public Section(Line line, Station upStation, Station downStation, int distance) {
        validateDistance(distance);
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section(Long id, Line line, Station upStation, Station downStation, int distance) {
        this(line, upStation, downStation, distance);
        this.id = id;
    }


    private void validateDistance(int distance) {
        if(distance <= 0) {
            throw new IllegalArgumentException("추가할 구간의 거리는 기존 구간의 거리보다 작아야 합니다.");
        }
    }

    public Long getId() {
        return id;
    }

    public Line getLine() {
        return line;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public int getDistance() {
        return distance;
    }

    public boolean existStation(Section other) {
        return upStation.equals(other.getUpStation()) || upStation.equals(other.getDownStation())
                || downStation.equals(other.getDownStation()) || downStation.equals(other.getUpStation());
    }

    public Section getSubSection(Section newSection) {
        if(upStation.equals(newSection.getUpStation())) {
            return new Section(line, newSection.getDownStation(), downStation, distance - newSection.getDistance());
        }
        return new Section(line, upStation, newSection.getUpStation(), distance - newSection.getDistance());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(line, section.line)
                && (Objects.equals(upStation, section.getUpStation()) && Objects.equals(downStation, section.getDownStation())
                || Objects.equals(upStation, section.getDownStation()) && Objects.equals(downStation, section.getUpStation()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(line, upStation, downStation, distance);
    }

}
