package subway.domain;

import subway.dto.LineRequest;
import subway.dto.SectionRequest;

import java.util.Objects;

public class Section {
    private Long id;
    private Line line;
    private Station upStation;
    private Station downStation;
    private int distance;
    private String pointType;

    public Section() {
    }

    public Section(Long id, Line line, Station upStation, Station downStation, int distance, String pointType) {
        validateDistance(distance);
        this.id = id;
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
        this.pointType = pointType;
    }

    public static Section of(Line line, Station upStation, Station downStation, int distance, String pointType) {
        return new Section(null, line, upStation, downStation, distance, pointType);
    }

    public static Section of(Line line, Station upStation, Station downStation, int distance) {
        return new Section(null, line, upStation, downStation, distance, Line.USE);
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

    public String getPointType() {
        return pointType;
    }

    public boolean isEndType() {
        return pointType.equals(Line.TAIL);
    }

    public boolean isHeadType() {
        return pointType.equals(Line.HEAD);
    }

    public boolean existStation(Section other) {
        return upStation.equals(other.getUpStation()) || upStation.equals(other.getDownStation())
                || downStation.equals(other.getDownStation()) || downStation.equals(other.getUpStation());
    }

    public Section getSubSection(Section newSection) {
        if(upStation.equals(newSection.getUpStation())) {
            return Section.of(line, newSection.getDownStation(), downStation, distance - newSection.getDistance(), pointType);
        }
        return Section.of(line, upStation, newSection.getUpStation(), distance - newSection.getDistance(), pointType);
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
