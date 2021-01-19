package subway.section;

public class Section {
    private Long id;
    private Long stationId;
    private Long lineId;
    private int distance;

    public Section(Long id, Long lineId, Long stationId, int distance) {
        this.id = id;
        this.stationId = stationId;
        this.lineId = lineId;
        this.distance = distance;
    }

    public Section(Long lineId, Long stationId, int distance) {
        this.stationId = stationId;
        this.lineId = lineId;
        this.distance = distance;
    }

    public int getDistance() {
        return distance;
    }

    public Long getLineId() {
        return lineId;
    }

    public Long getStationId() {
        return stationId;
    }

    public int compareDistance(Section otherSection) {
        return this.getDistance() - otherSection.getDistance();
    }

}
