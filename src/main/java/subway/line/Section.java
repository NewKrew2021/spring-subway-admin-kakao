package subway.line;

public class Section {

    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public Section(Long upStationId, Long downStationId) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
    }

    public Section(Long upStationId, Long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(Long lineId, Long upStationId, Long downStationId, int distance) {
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(Long id, Long lineId, Long upStationId, Long downStationId, int distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    private Long getId() {
        return id;
    }

    private Long getLineId() {
        return lineId;
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

    public boolean contains(Long id) {
        return upStationId == id || downStationId == id;
    }
}
