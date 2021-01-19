package subway.dto;

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

    public boolean canInsertMatchingUpStation(Section newSection) {
        return this.getUpStationId().equals(newSection.getUpStationId()) &&
                !this.getDownStationId().equals(newSection.getDownStationId()) &&
                this.getDistance() > newSection.getDistance();
    }

    public boolean canInsertMatchingDownStation(Section newSection) {
        if (this.getDownStationId().equals(newSection.getDownStationId())
                && !this.getUpStationId().equals(newSection.getUpStationId())
                && this.getDistance() > newSection.getDistance()) {
            return true;
        }
        return false;
    }
}
