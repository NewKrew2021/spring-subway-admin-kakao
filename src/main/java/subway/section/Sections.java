package subway.section;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Sections {
    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = Collections.unmodifiableList(sections);
    }

    public List<Long> getSortedStationIds(long startStationId) {
        Map<Long, Section> sectionMap = sections.stream()
                .collect(Collectors.toMap(Section::getUpStationId, section -> section));
        List<Long> stationIds = new ArrayList<>();
        Section curSection = sectionMap.get(startStationId);
        stationIds.add(curSection.getUpStationId());
        while (curSection != null) {
            stationIds.add(curSection.getDownStationId());
            curSection = sectionMap.getOrDefault(curSection.getDownStationId(), null);
        }
        return stationIds;
    }
}
