package subway.section;

public class Section {
    private Long id;
    private Long upStationId;
    private Long downStationId;
    private int distance;
    private Long lineId;

    public Section() {
    }

    public Section(Long id, Long upStationId, Long downStationId, int distance, Long lineId) {
        this.id = id;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
        this.lineId = lineId;
    }

    public Section(Long upStationId, Long downStationId, int distance, Long lineId) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
        this.lineId = lineId;
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

    public int getDistance() {
        return distance;
    }
}
