package subway.section.domain;

public class Section {
    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public Section(Long lineId, Long upStationId, Long downStationId, int distance) {
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(Long id, Long lineId, Long upStationId, Long downStationId, int distance) {
        this(lineId, upStationId, downStationId, distance);
        this.id = id;

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

    public Long getId() {
        return id;
    }

    public void checkValidInsert(Section currentSection) {
        checkStations(currentSection);
        checkDistance(currentSection);
    }

    private void checkStations(Section currentSection) {
        if (this.upStationId == currentSection.getUpStationId() && this.downStationId == currentSection.getDownStationId()) {
            throw new IllegalArgumentException("상행역과 하행역 모두 일치하여 구간 추가할 수 없습니다.");
        }
    }

    private void checkDistance(Section currentSection) {
        if (this.distance >= currentSection.getDistance()) {
            throw new IllegalArgumentException("추가하고자 하는 구간이 기존 역 사이의 거리보다 크거나 같아 구간을 추가할 수 없습니다.");
        }
    }
}
