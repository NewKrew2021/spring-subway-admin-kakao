package subway.section;

import subway.section.exceptions.DuplicateSectionException;
import subway.section.exceptions.EmptySectionsException;
import subway.section.exceptions.InvalidAddSectionException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class Sections {

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        validateSections(sections);
        this.sections = sections;
    }

    private void validateSections(List<Section> sections) {
        if (sections.size() == 0) {
            throw new EmptySectionsException();
        }
    }

    public void validateAddSection(Section newSection) {
        AtomicBoolean isExistUpStation = new AtomicBoolean(false);
        AtomicBoolean isExistDownStation = new AtomicBoolean(false);

        sections.stream()
                .forEach(section -> {
                    isExistUpStation.compareAndSet(!section.containStation(newSection.getUpStationId()), true);
                    isExistDownStation.compareAndSet(!section.containStation(newSection.getDownStationId()), true);
                });

        if (isExistUpStation.get() && isExistDownStation.get()) {
            throw new DuplicateSectionException("추가하려는 구간이 이미 노선에 존재합니다.");
        }

        if (!isExistUpStation.get() && !isExistDownStation.get()) {
            throw new InvalidAddSectionException("추가하려는 구간에 연결된 역이 존재하지 않습니다.");
        }
    }

    public boolean isFirstSection(Section newSection) {
        boolean isNotMatchAnyDownStation = sections.stream()
                .noneMatch(section -> section.getDownStationId().equals(newSection.getDownStationId()));
        boolean isOriginUpStationMatchWithNewDownStation = sections.stream()
                .anyMatch(section -> section.getUpStationId().equals(newSection.getDownStationId()));

        return isNotMatchAnyDownStation && isOriginUpStationMatchWithNewDownStation;
    }

    public boolean isLastSection(Section newSection) {
        boolean isNotMatchAnyUpStation = sections.stream()
                .noneMatch(section -> section.getUpStationId().equals(newSection.getUpStationId()));
        boolean isOriginDownStationMatchWithNewUpStation = sections.stream()
                .anyMatch(section -> section.getDownStationId().equals(newSection.getUpStationId()));

        return isNotMatchAnyUpStation && isOriginDownStationMatchWithNewUpStation;
    }

    public Section getMatchedUpStation(Section newSection) {
        return sections.stream()
                .filter(section -> section.getUpStationId().equals(newSection.getUpStationId()))
                .findFirst()
                .orElse(null);
    }

    public Section getMatchedDownStation(Section newSection) {
        return sections.stream()
                .filter(section -> section.getDownStationId().equals(newSection.getDownStationId()))
                .findFirst()
                .orElse(null);
    }

    public List<Section> getSectionsContainStationId(Long stationId) {
        return sections.stream()
                .filter(section -> section.getUpStationId().equals(stationId) || section.getDownStationId().equals(stationId))
                .collect(Collectors.toList());
    }

    public List<Long> getStationIds() {
        Map<Long, Section> sectionOfUpStation = new HashMap<>();
        for (Section section : sections) {
            sectionOfUpStation.put(section.getUpStationId(), section);
        }

        Section firstSection = findFirstSection(sectionOfUpStation);

        List<Long> stationIds = new ArrayList<>();
        stationIds.add(firstSection.getUpStationId());
        for (Section iter = firstSection; iter != null; iter = sectionOfUpStation.get(iter.getDownStationId())) {
            stationIds.add(iter.getDownStationId());
        }

        return stationIds;
    }

    private Section findFirstSection(Map<Long, Section> sectionOfUpStation) {
        Map<Long, Section> temp = new HashMap<>(sectionOfUpStation);

        for (Section section : sections) {
            temp.remove(section.getDownStationId());
        }

        return temp.values()
                .stream()
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    public int size() {
        return sections.size();
    }
}
