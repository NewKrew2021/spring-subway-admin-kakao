package subway.line;

import subway.station.Station;

public class Section {
    private Long id;
    private Long lineId;
    private Station upStation;
    private Station downStation;
    private int distance;

    public Section(Station upStation,
                   Station downStation,
                   int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section(Long lineId,
                   Station upStation,
                   Station downStation,
                   int distance) {
        this(upStation, downStation, distance);
        this.lineId = lineId;
    }

    public Section(Long id,
                   Long lineId,
                   Station upStation,
                   Station downStation,
                   int distance) {
        this(lineId, upStation, downStation, distance);
        this.id = id;
    }

    public Long getLineId(){
        return lineId;
    }

    public Long getId() {
        return id;
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

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", lineId=" + lineId +
                ", upStation=" +( upStation!=null? upStation.toString(): "null")+
                ", downStation=" + ( downStation!=null? downStation.toString(): "null") +
                ", distance=" + distance +
                '}';
    }
}
