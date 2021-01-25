package subway.line.domain;

import subway.line.dto.SectionRequest;

public class SectionFactory {
    public static Section create(Long lineId, SectionRequest sectionRequest) {
        return new Section(lineId, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());
    }
}
