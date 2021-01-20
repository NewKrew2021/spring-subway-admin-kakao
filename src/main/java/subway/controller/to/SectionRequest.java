package subway.controller.to;

import subway.domain.Section;

public class SectionRequest {
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public SectionRequest() {
    }

    public SectionRequest(Long upStationId, Long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
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

    public Section getSection(Long lineId) {
        return new Section(lineId,
                getUpStationId(),
                getDownStationId(),
                getDistance(),
                Section.NOT_FIRST_SECTION,
                Section.NOT_LAST_SECTION);
    }
}
