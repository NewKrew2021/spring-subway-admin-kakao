package subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Sections {
    List<Section> sections;

    public Sections() {
        sections = new ArrayList<>();
    }

    public Sections(List<Section> sections) {
        Long cur = getStartStation(sections);
        List<Section> orderedSections = new LinkedList<>();

        while (orderedSections.size() != sections.size()) {
            Long finalCur = cur;
            Section section = sections.stream()
                    .filter(sec -> sec.getUpStationId().equals(finalCur))
                    .findFirst()
                    .get();
            System.out.println(section);
            orderedSections.add(section);
            cur = section.getDownStationId();
        }
        this.sections = Collections.unmodifiableList(orderedSections);
    }

    private Long getStartStation(List<Section> sections) {
        for (Section section : sections) {
            boolean connectFlag = false;
            for (Section section1 : sections) {
                if (section.getUpStationId().equals(section1.getDownStationId())) {
                    connectFlag = true;
                }
            }
            if (!connectFlag) {
                return section.getUpStationId();
            }
        }
        return -1L;
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
