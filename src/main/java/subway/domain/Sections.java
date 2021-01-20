package subway.domain;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Sections {
    public static final int FIRST = 0;
    public static final int SECOND = 1;
    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public Map<Long, Section> getOrderedSections() {
        return sections.stream()
                .collect(Collectors.toMap(Section::getUpStationId, Function.identity()));
    }

    public Map<Long, Section> getReverseOrderedSections() {
        return sections.stream()
                .collect(Collectors.toMap(Section::getDownStationId, Function.identity()));
    }

    public Sections getContainSections(Long stationId) {
        return new Sections(sections.stream()
                .filter(section -> section.getUpStationId().equals(stationId) || section.getDownStationId().equals(stationId))
                .collect(Collectors.toList()));
    }

    public Section getMergeSection(Long stationId) {
        return sections.get(FIRST).merge(sections.get(SECOND), stationId);
    }

    public Section getDeleteSection() {
        return sections.get(SECOND);
    }
}
