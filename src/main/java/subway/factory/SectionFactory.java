package subway.factory;

import subway.domain.Section;
import subway.domain.Station;
import subway.dto.SectionRequest;

public class SectionFactory {

    public static Section getSection(SectionRequest sectionRequest, Long lineId) {
        return new Section(new Station(sectionRequest.getUpStationId()),new Station(sectionRequest.getDownStationId()), sectionRequest.getDistance(), lineId);
    }
}
