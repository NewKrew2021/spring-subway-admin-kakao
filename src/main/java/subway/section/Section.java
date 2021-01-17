package subway.section;

public class Section {
    private Long id, upStationId, downStationId, lineId;
    private int distance;

    public Section() {

    }

    public Section(Long id, Long lineId, Long upStationId, Long downStationId, int distance) {
        if(distance <= 0) throw new RuntimeException();
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(Long upStationId, Long downStationId, Long lineId, int distance) {
        if(distance <= 0) throw new RuntimeException();
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.lineId = lineId;
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Long getLineId() {
        return lineId;
    }

    public int getDistance() {
        return distance;
    }
}
