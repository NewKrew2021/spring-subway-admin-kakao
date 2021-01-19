package subway.section;

public class Section {
    private Long id;
    private Long stationId;
    private Long lineId;
    private RelativeDistance relativeDistance;

    public Section(Long id, Long lineId, Long stationId, int distance) {
        this.id = id;
        this.stationId = stationId;
        this.lineId = lineId;
        this.relativeDistance = new RelativeDistance(distance);
    }

    public Section(Long lineId, Long stationId, int distance) {
        this.stationId = stationId;
        this.lineId = lineId;
        this.relativeDistance = new RelativeDistance(distance);
    }
    public RelativeDistance getRelativeDistance() {
        return relativeDistance;
    }

    public int getRelativeDistanceByInteger() {
        return relativeDistance.getRelativeDistance();
    }

    public Long getLineId() {
        return lineId;
    }

    public Long getStationId() {
        return stationId;
    }

    public int compareDistance(Section otherSection) {
        return this.relativeDistance.calculateDistanceDifference(otherSection.relativeDistance);
    }

}
