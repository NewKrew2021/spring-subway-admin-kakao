package subway.line;

import subway.station.Station;

public class Section {

    private final Station upStation;
    private final Station downStation;
    private final int distance;

    public Section(Station upStation, Station downStation, int distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException("구간 길이는 0보다 작거나 같은 수 없습니다.");
        }
        if (upStation == null || downStation == null) {
            distance = Integer.MAX_VALUE;
        }
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public boolean shareUpStation(Section counter){
        return counter.isUpStation(upStation);
    }

    public boolean shareDownStation(Section counter){
        return counter.isDownStation(downStation);
    }

    public boolean isUpStation(Station station) {
        return upStation.equals(station);
    }

    public boolean isDownStation(Station station) {
        return downStation.equals(station);
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
}
