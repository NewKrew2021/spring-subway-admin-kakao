package subway.section;

import subway.line.Line;

import java.util.*;

public class Sections {
    List<Section> sections;

    public static final Long NOT_EXIST = -1L;

    Sections() {
        sections = new ArrayList<>();
    }

    Sections(List<Section> sections) {
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

    public Long findStationExistBySection(Section section) {
        for (Section sec : sections) {
            if (sec.getUpStationId().equals(section.getUpStationId()) ||
                    sec.getDownStationId().equals(section.getUpStationId())) {
                return section.getUpStationId();
            }
            if (sec.getUpStationId().equals(section.getDownStationId()) ||
                    sec.getDownStationId().equals(section.getDownStationId())) {
                return section.getDownStationId();
            }
        }

        return NOT_EXIST;
    }

    public boolean isPossibleToDelete(Long stationId) {
        return findSectionByStationId(stationId) != null && sections.size() > 1;
    }

    public List<Section> getSections() {
        return sections;
    }

    public Sections getOrderedSection(Line line) {
        Long cur = line.getUpStationId();
        Long dest = line.getDownStationId();
        List<Section> orderedSections = new LinkedList();
        Set<Long> visit = new HashSet<>();

        while (!cur.equals(dest) && visit.add(cur)) {
            Section section = findSectionByUpStationId(cur);
            orderedSections.add(section);
            cur = section.getDownStationId();
        }

        if(visit.contains(cur))
            throw new RuntimeException("반복되는 구간이 존재합니다.");

        return new Sections(orderedSections);
    }

    public boolean checkSectionExist(Section section){
        if (isContainingSameSection(section) || findStationExistBySection(section) == Sections.NOT_EXIST) {
            return false;
        }
        return true;
    }

    public boolean isContainingSameSection(Section section) {
        return sections.contains(section);
    }
    //판교 -       강남 - 광교
    //      정자 - 강남
    public Section findNextSection(Section section){
        return findSectionByUpStationId(section.getUpStationId());
    }

    public Section findPrevSection(Section section){
        return findSectionByDownStationId(section.getDownStationId());
    }
}
