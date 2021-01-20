package subway.domain;

import subway.exception.DataEmptyException;

import java.util.*;

public class Sections {
    List<Section> sections;
    List<Station> stations;

    public Sections(List<Section> sections) {
        Long cur = getStartStationId(sections);
        Long dest = getEndStationId(sections);
        List<Section> orderedSections = new LinkedList<>();
        while (!cur.equals(dest)) {
            Long finalCur = cur;
            Section section = sections.stream()
                    .filter(sec -> sec.getUpStationId().equals(finalCur))
                    .findFirst()
                    .get();
            orderedSections.add(section);
            cur = section.getDownStationId();
        }
        this.sections = Collections.unmodifiableList(orderedSections);
    }

    public Sections(List<Section> sections, List<Station> stations) {
        this(sections);
        this.stations = stations;
    }

    private Long getStartStationId(List<Section> sections) {
        return sections.stream()
                .filter(section -> isInSectionUpStation(sections, section))
                .findFirst()
                .orElseThrow(DataEmptyException::new)
                .getUpStationId();
    }

    private boolean isInSectionUpStation(List<Section> sections, Section section) {
        return sections.stream()
                .noneMatch(section1 -> section.getUpStationId().equals(section1.getDownStationId()));
    }

    private Long getEndStationId(List<Section> sections) {
        return sections.stream()
                .filter(section -> isInSectionDownStation(sections, section))
                .findFirst()
                .orElseThrow(DataEmptyException::new)
                .getDownStationId();
    }

    private boolean isInSectionDownStation(List<Section> sections, Section section) {
        return sections.stream()
                .noneMatch(section1 -> section.getDownStationId().equals(section1.getUpStationId()));
    }

    public Long getStartStation() {
        return this.sections
                .get(0)
                .getUpStationId();
    }

    public Long getEndStation() {
        return this.sections
                .get(this.sections.size() - 1)
                .getDownStationId();
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

    public boolean isCanSaveSection(Section section) {
        return sections.stream()
                .anyMatch(sec -> sec.getUpStationId().equals(section.getUpStationId())
                        || sec.getDownStationId().equals(section.getUpStationId())
                        || sec.getUpStationId().equals(section.getDownStationId())
                        || sec.getDownStationId().equals(section.getDownStationId()));
    }

    public boolean isPossibleToDelete() {
        return sections.size() > 1;
    }

    public List<Section> getSections() {
        return sections;
    }

    public List<Long> getStationIds() {
        Set<Long> stationIds = new LinkedHashSet<>();
        sections.forEach(section -> {
                    stationIds.add(section.getUpStationId());
                    stationIds.add(section.getDownStationId());
                });
        return new ArrayList<>(stationIds);
    }

    public boolean isExistUpStationAndMiddleSection(Section section) {
        return findSectionByUpStationId(section.getUpStationId()) != null && !getEndStation().equals(section.getDownStationId());
    }

    public boolean isExistDownStationAndMiddleSection(Section section) {
        return findSectionByDownStationId(section.getDownStationId()) != null && !getStartStation().equals(section.getDownStationId());
    }

    public Long getLineId() {
        return sections.get(0).getLineId();
    }

    public List<Station> getStations() {
        return stations;
    }
}
