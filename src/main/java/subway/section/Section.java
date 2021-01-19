package subway.section;

public class Section {

    public static final Long WRONG_ID = -1L;
    public static final Section DO_NOT_EXIST_SECTION = null;

    private Long id;
    private Long lineId;
    private Long stationId;
    private int distance;
    private Long nextStationId;

    public Section(Long id, Long lineId, Long stationId, int distance, Long nextStationId) {
        this.id = id;
        this.lineId = lineId;
        this.stationId = stationId;
        this.distance = distance;
        this.nextStationId = nextStationId;
    }

    public Section(Long lineId, Long stationId, int distance, Long nextStationId) {
        this.lineId = lineId;
        this.stationId = stationId;
        this.distance = distance;
        this.nextStationId = nextStationId;
    }

    public long getId() {
        return id;
    }

    public long getLineId() {
        return lineId;
    }

    public long getStationId() {
        return stationId;
    }

    public int getDistance() {
        return distance;
    }

    public Long getNextStationId() {
        return nextStationId;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public void setNextStation(Long nextId) {
        this.nextStationId = nextId;
    }
}

