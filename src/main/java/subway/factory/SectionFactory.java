package subway.factory;

import subway.domain.Section;
import subway.dto.SectionRequest;

public class SectionFactory {

    public static Section getSection(SectionRequest sectionRequest, Long lineId) {
        return new Section(sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance(), lineId);
    }
}
