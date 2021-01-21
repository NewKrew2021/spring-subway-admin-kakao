package subway.section;

import java.util.LinkedList;
import java.util.List;

public class Sections {
    List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public LinkedList<Long> getSortingStationId() {
        LinkedList<Long> linkedList = new LinkedList<>();
        Section currentSection = findSectionByNextId(Section.WRONG_ID);

        while(linkedList.size() != sections.size()) {
            linkedList.addFirst(currentSection.getStationId());
            currentSection = findSectionByNextId(currentSection.getStationId());
        }

        return linkedList;
    }

    private Section findSectionByNextId(Long nextId) {
        return sections.stream()
                .filter(section -> section.getNextStationId() == nextId)
                .findAny()
                .orElse(null);
    }

}
