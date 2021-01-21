package subway.section.domain;

public class Section {
    private Long id;
    private Long lineId;
    private Long stationId;
    private int distance;

    public Section(){}

    public Section(Long lineId, Long stationId, int distance) {
        this.lineId = lineId;
        this.stationId = stationId;
        this.distance = distance;
    }

    public Section(Long id, Long lineId, Long stationId, int distance) {
        this(lineId, stationId, distance);
        this.id = id;
    }

    public Long getLineId() { return lineId;}

    public int getDistance() {
        return distance;
    }

    public Long getStationId() {return stationId;}

    public Long getId(){return id;}

    public void setDistance(int distance){ this.distance = distance; }
}
