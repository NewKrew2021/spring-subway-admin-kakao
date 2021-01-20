package subway.section;

import java.util.List;
import java.util.stream.Collectors;

public class Sections {

    private final List<? extends Section> sections;

    public Sections(List<? extends Section> sections) {
        this.sections = sections;
    }

    public <T extends Section> T findHeadSection() {
        return (T) sections.stream()
                .filter(Section::isHeadSection)
                .findAny()
                .get();
    }

    public <T extends Section> T findRearOfGivenStation(Long stationId) {
        return (T) sections.stream()
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

    public boolean hasSameSection(Section another) {
        return sections.stream()
                .anyMatch(section -> section.equals(another) || section.isExist(another));
    }

    public int size() {
        return sections.size();
    }

    public List<Long> getSectionIds() {
        return sections.stream()
                .mapToLong(Section::getId)
                .boxed()
                .collect(Collectors.toList());
    }

}
