package subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Sections {
    List<Section> sections;

    public Sections() {
        sections = new ArrayList<>();
    }

    public Sections(List<Section> sections) {
        this.sections = Collections.unmodifiableList(sections);
    }

    public Section findSectionByUpStationId(Long id) {
        return sections.stream()
                .filter(sec -> sec.getUpStationId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public Section findSectionByDownStationId(Long id) {
        return sections.stream()
                .filter(sec -> sec.getDownStationId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public Section findSectionByStationId(Long id) {
        return sections.stream()
                .filter(sec -> sec.getUpStationId().equals(id) || sec.getDownStationId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public Long findStationExist(Section section) {
        for (Section sec : sections) {
            if (sec.getUpStationId().equals(section.getUpStationId()) || sec.getDownStationId().equals(section.getUpStationId())) {
                return section.getUpStationId();
            }
            if (sec.getUpStationId().equals(section.getDownStationId()) || sec.getDownStationId().equals(section.getDownStationId())) {
                return section.getDownStationId();
            }
        }

        return -1L;
    }

    public boolean isPossibleToDelete() {
        return sections.size() > 1;
    }

    public List<Section> getSections() {
        return sections;
    }

    @Override
    public String toString() {
        return "Sections{" +
                "sections=" + sections +
                '}';
    }
}
