package subway.line;

import java.util.Map;

public enum SectionStatus {
    UP_STATION_MATCHING, DOWN_STATION_MATCHING, NONE_MATCHING;

    public static SectionStatus getSectionStatus(Map<Long, Long> sections, Section section) {
        if (sections.containsKey(section.getUpStationId())) {
            return UP_STATION_MATCHING;
        }
        if (sections.containsValue(section.getDownStationId())) {
            return DOWN_STATION_MATCHING;
        }
        return NONE_MATCHING;
    }
}
