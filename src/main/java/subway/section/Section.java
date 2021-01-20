package subway.section;

import subway.exceptions.IllegalSectionCreateException;

public class Section {
    private Long id, lineId;
    private Long upStationId, downStationId;
    private int distance;

    public Section() {

    }

    public Section(Long id, Long lineId, Long upStationId, Long downStationId, int distance) {
        validationCheck(upStationId, downStationId, distance);

        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(Long upStationId, Long downStationId, Long lineId, int distance) {
        validationCheck(upStationId, downStationId, distance);

        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.lineId = lineId;
        this.distance = distance;
    }

    public Section(Long lineId, SectionDto sectionDto) {
        this.lineId = lineId;
        this.upStationId = sectionDto.getUpStationId();
        this.downStationId = sectionDto.getDownStationId();
        this.distance = sectionDto.getDistance();
    }

    public static void validationCheck(Long upStationId, Long downStationId, int distance) {
        if(distance <= 0) throw new IllegalSectionCreateException("distance는 0보다 커야 합니다.");
        if(upStationId == downStationId) throw new IllegalSectionCreateException("upStationId 와 downStationId는 서로 달라야 합니다.");
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

    public Long getLineId() {
        return lineId;
    }

    public int getDistance() {
        return distance;
    }
}
