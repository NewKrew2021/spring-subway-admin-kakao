package subway.section;

public class Section {
    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public Section() {
    }

    private Section(Long id, Long lineId, Long upStationId, Long downStationId, int distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    private Section(Long lineId, Long upStationId, Long downStationId, int distance) {
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public static Section of(Long id, Section section) {
        return new Section(id, section.getLineId(), section.getUpStationId(), section.getDownStationId(), section.getDistance());
    }

    public static Section of(Long id, Long lineId, Long upStationId, Long downStationId, int distance) {
        return new Section(id, lineId, upStationId, downStationId, distance);
    }

    public static Section of(Long lineId, Long upStationId, Long downStationId, int distance) {
        return new Section(lineId, upStationId, downStationId, distance);
    }

    public boolean equalsWithUpStationId(Long stationId) {
        return stationId.equals(upStationId);
    }

    public boolean equalsWithDownStationId(Long stationId) {
        return stationId.equals(downStationId);
    }

    public boolean containStation(Long stationId) {
        return equalsWithUpStationId(stationId) || equalsWithDownStationId(stationId);
    }

    public boolean isShorterThan(Section section) {
        return distance <= section.getDistance();
    }

    public Long getId() {
        return id;
    }

    public Long getLineId() {
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
}
