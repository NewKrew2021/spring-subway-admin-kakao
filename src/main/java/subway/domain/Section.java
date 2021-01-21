package subway.domain;

public class Section {
    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public Section() {

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

    public boolean isInsert(Section newSection) {
        return validateDistance(newSection) && (isMatchedOnlyUpStationId(newSection) || isMatchedOnlyDownStationId(newSection));
    }

    private boolean validateDistance(Section newSection) {
        return this.distance > newSection.distance;
    }

    private boolean isMatchedOnlyUpStationId(Section newSection) {
        return this.upStationId.equals(newSection.upStationId) && !this.downStationId.equals(newSection.downStationId);
    }

    private boolean isMatchedOnlyDownStationId(Section newSection) {
        return this.downStationId.equals(newSection.downStationId) && !this.upStationId.equals(newSection.upStationId);
    }

    public boolean isContainStation(Long stationId) {
        return upStationId.equals(stationId) || downStationId.equals(stationId);
    }

    public void mergeSection(Section section) {
        if (upStationId.equals(section.downStationId)) {
            this.distance += section.distance;
            this.upStationId = section.upStationId;
        }
        if (this.downStationId.equals(section.upStationId)) {
            this.distance += section.distance;
            this.downStationId = section.downStationId;
        }
    }

    public void modifyMatchedSection(Section newSection) {
        if (isMatchedOnlyUpStationId(newSection)) {
            this.upStationId = newSection.downStationId;
            this.distance -= newSection.distance;
        }
        if (isMatchedOnlyDownStationId(newSection)) {
            this.downStationId = newSection.upStationId;
            this.distance -= newSection.distance;
        }
    }

    public Long getId() {
        return this.id;
    }

    public Long getLineId() {
        return this.lineId;
    }

    public Long getUpStationId() {
        return this.upStationId;
    }

    public Long getDownStationId() {
        return this.downStationId;
    }

    public int getDistance() {
        return this.distance;
    }


}
