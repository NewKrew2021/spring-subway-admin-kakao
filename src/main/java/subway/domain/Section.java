package subway.domain;

public class Section {
    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private int distance;
    private boolean firstSection;
    private boolean lastSection;

    public Section(Long lineId, Long upStationId, Long downStationId, int distance, boolean firstSection, boolean lastSection) {
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
        this.firstSection = firstSection;
        this.lastSection = lastSection;
    }

    public Section(Long id, Long lineId, Long upStationId, Long downStationId, int distance, boolean firstSection, boolean lastSection) {
        this(lineId, upStationId, downStationId, distance, firstSection, lastSection);
        this.id = id;
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

    public boolean isFirstSection() {
        return firstSection;
    }

    public void setFirstSection(boolean firstSection) {
        this.firstSection = firstSection;
    }

    public boolean isLastSection() {
        return lastSection;
    }

    public void setLastSection(boolean lastSection) {
        this.lastSection = lastSection;
    }

    public Section merge(Section section, Long stationId) {
        if (this.upStationId.equals(stationId)) {
            this.upStationId = section.getUpStationId();
        }

        if (this.downStationId.equals(stationId)) {
            this.downStationId = section.getDownStationId();
        }

        this.distance += section.getDistance();

        return this;
    }
}
