package subway.line;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Sections {

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public Section findHeadSection() {
        return sections.stream()
                .filter(Section::isHeadSection)
                .findAny()
                .get();
    }

    public Section findTailSection() {
        return sections.stream()
                .filter(Section::isTailSection)
                .findAny()
                .get();
    }

    public Section findRearOfGivenSection(Long stationId) {
        return sections.stream()
                .filter(section -> section.isUpStation(stationId))
                .findAny()
                .get();
    }

    public Section findFrontOfGivenStation(Long stationId) {
        return sections.stream()
                .filter(section -> section.isDownStation(stationId))
                .findAny()
                .get();
    }

    public int size() {
        return sections.size();
    }

    public boolean contains(Section section) {
        return sections.contains(section);
    }

    public List<Long> getSectionIds() {
        return sections.stream()
                .mapToLong(Section::getId)
                .boxed()
                .collect(Collectors.toList());
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }

}
