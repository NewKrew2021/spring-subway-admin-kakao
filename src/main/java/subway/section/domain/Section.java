package subway.section.domain;

public class Section {
    private Long id;
    private Long stationId;
    private Long lineId;
    private int relativePosition;

    public Section(Long id, Long lineId, Long stationId, int distance) {
        this.id = id;
        this.stationId = stationId;
        this.lineId = lineId;
        this.relativePosition = distance;
    }

    public Section(Long lineId, Long stationId, int distance) {
        this.stationId = stationId;
        this.lineId = lineId;
        this.relativePosition = distance;
    }

    public Long getStationId() {
        return stationId;
    }

    public int comparePosition(Section otherSection) {
        return this.relativePosition - otherSection.getRelativePosition();
    }

    public int getRelativePosition() {
        return relativePosition;
    }

    public Long getLineId() {
        return lineId;
    }

    @Override
    public String toString() {
        return "Section{" +
                "stationId=" + stationId +
                '}';
    }
}
