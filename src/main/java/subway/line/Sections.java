package subway.line;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Sections {
    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = Collections.unmodifiableList(sections);
    }

    public List<Long> getSortedStationIds() {
        Map<Long, Section> upToDownStations = sections.stream()
                .collect(Collectors.toMap(Section::getUpStationId, Function.identity()));
        Section sectionIterator = getFirstSection();

        List<Long> ids = new ArrayList<>();
        while (!sectionIterator.isDownTerminal()) {
            sectionIterator = upToDownStations.get(sectionIterator.getDownStationId());
            ids.add(sectionIterator.getUpStationId());
        }

        return ids;
    }

    public boolean hasOnlyOne() {
        return sections.size() <= 3;
    }

    public boolean empty() {
        return sections.size() <= 2;
    }

    public boolean isLastSection(Section downSection) {
        return getLastSection().getUpStationId() == downSection.getDownStationId();
    }

    public boolean isFirstSection(Section upSection) {
        return getFirstSection().getDownStationId() == upSection.getUpStationId();
    }

    private Section getFirstSection() {
        return sections.stream()
                .filter(Section::isUpTerminal)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    private Section getLastSection() {
        return sections.stream()
                .filter(Section::isDownTerminal)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
