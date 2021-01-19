package subway.domain;

public class Section {
    private Long id;
    private Line line;
    private Station upStation;
    private Station downStation;
    private int distance;

    public Section() {

    }

    public Section(Long id, Line line, Station upStation, Station downStation, int distance) {
        this(line, upStation, downStation, distance);
        this.id = id;
    }

    public Section(Line line, Station upStation, Station downStation, int distance) {
        if (distance <= 0) throw new IllegalArgumentException("Distance should be positive integer.");
        if (upStation.getId().equals(downStation.getId()))
            throw new IllegalArgumentException("Endpoint should not be the same station");
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
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

    public boolean hasStation(Station station) {
        return upStation.equals(station) || downStation.equals(station);
    }

    public boolean hasSameUpStation(Section section) {
        return upStation.getId().equals(section.getUpStation().getId());
    }

    public boolean hasSameDownStation(Section section) {
        return downStation.getId().equals(section.downStation.getId());
    }

    public int getDistanceDifference(Section section) {
        return distance - section.getDistance();
    }

    public Section connectDownward(Section section) {
        if (!line.equals(section.getLine())) throw new RuntimeException();

        return new Section(
                line,
                upStation,
                section.getDownStation(),
                distance + section.getDistance()
        );
    }

    public Section splitBy(Section section) {
        validateSplitCondition(section);

        int newDistance = getDistanceDifference(section);
        Station newUpStation = hasSameUpStation(section) ? section.downStation : upStation;
        Station newDownStation = hasSameUpStation(section) ? downStation : section.upStation;

        return new Section(line, newUpStation, newDownStation, newDistance);
    }

    private void validateSplitCondition(Section section) {
        if (getDistanceDifference(section) <= 0) {
            throw new RuntimeException();
        }
        if (hasSameDownStation(section) == hasSameUpStation(section)) {
            throw new RuntimeException();
        }
    }

}
