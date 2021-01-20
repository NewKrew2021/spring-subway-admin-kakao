package subway.line.domain;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Sections {
    public static final int FIRST = 0;
    public static final int SECOND = 1;
    private List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public Map<Long, Section> getOrderedSections() {
        return sections.stream()
                .collect(Collectors.toMap(Section::getUpStationId, section -> section));
    }

    public Map<Long, Section> getReverseOrderedSections() {
        return sections.stream()
                .collect(Collectors.toMap(Section::getDownStationId, section -> section));
    }

    public Section getMergeSection(Long stationId) {
        return sections.get(FIRST).merge(sections.get(SECOND), stationId);
    }

    public Section getDeleteSection() {
        return sections.get(SECOND);
    }
}
