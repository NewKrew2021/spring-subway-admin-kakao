package subway.section;

import java.util.Arrays;
import java.util.List;

public class SectionFactory {
    public static List<Section> createInitialSections(Long lineId, Long upStationId, Long downStationId, int distance) {
        return Arrays.asList(
                new Section(lineId, upStationId, downStationId, distance),
                new Section(lineId, Section.TERMINAL_ID, upStationId, Section.INF),
                new Section(lineId, downStationId, Section.TERMINAL_ID, Section.INF)
        );
    }
}
