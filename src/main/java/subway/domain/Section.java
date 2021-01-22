package subway.domain;

public class Section {
    public static final boolean LAST_SECTION = true;
    public static final boolean FIRST_SECTION = true;
    public static final boolean NOT_FIRST_SECTION = false;
    public static final boolean NOT_LAST_SECTION = false;
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

    public boolean isNewLastSection(Section section) {
        return upStationId.equals(section.downStationId);
    }

    public boolean isNewFirstSection(Section section) {
        return downStationId.equals(section.upStationId);
    }

    public Section merge(Section section, Long stationId) {
        if (upStationId.equals(stationId)) {
            upStationId = section.upStationId;
        }

        if (downStationId.equals(stationId)) {
            downStationId = section.downStationId;
        }

        distance += section.distance;
        firstSection = firstSection || section.firstSection;
        lastSection = lastSection || section.lastSection;

        return this;
    }

    public boolean equalsWithUpStation(Long stationId) {
        return upStationId.equals(stationId);
    }

    public boolean equalsWithDownStation(Long stationId) {
        return downStationId.equals(stationId);
    }
}
