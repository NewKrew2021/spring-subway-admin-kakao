package subway.section.entity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class LineSections {
    private final List<Section> sections;

    public LineSections(List<Section> sections) {
        validate(sections);
        this.sections = Collections.unmodifiableList(sections);
    }

    private void validate(List<Section> sections) {
        if (isEmpty(sections)) {
            throw new IllegalArgumentException("비어있는 구간 리스트를 입력받을 수 없습니다.");
        }
        if (isNotUnited(sections)) {
            throw new IllegalArgumentException("한 줄기로 이어지지 않은 구간 리스트를 입력받을 수 없습니다.");
        }
    }

    private boolean isEmpty(List<Section> sections) {
        return sections == null || sections.isEmpty();
    }

    private boolean isNotUnited(List<Section> sections) {
        int idx = 1;
        while (idx < sections.size() && isUnited(sections.get(idx - 1), sections.get(idx))) {
            ++idx;
        }
        return idx != sections.size();
    }

    private boolean isUnited(Section upSection, Section downSection) {
        return (upSection.getDownStationId() == downSection.getUpStationId())
                && (upSection.getLineId() == downSection.getLineId());
    }

    public Section getMergedSection() {
        return new Section(
                getLineId(),
                getUpEndStationId(),
                getDownEndStationId(),
                getSumOfDistance()
        );
    }

    private long getLineId() {
        return sections.get(0).getLineId();
    }

    private long getUpEndStationId() {
        return sections.get(0).getUpStationId();
    }

    private long getDownEndStationId() {
        return sections.get(sections.size() - 1).getDownStationId();
    }

    private int getSumOfDistance() {
        return sections.stream()
                .map(Section::getDistance)
                .reduce(Integer::sum)
                .orElseThrow(AssertionError::new);
    }

    public boolean isNotDeletable() {
        return sections.size() == 1;
    }

    public boolean isExtendable(Section insertSection) {
        return !isCycle(insertSection) && isConnected(insertSection);
    }

    private boolean isCycle(Section insertSection) {
        return (getUpEndStationId() == insertSection.getDownStationId())
                && (getDownEndStationId() == insertSection.getUpStationId());
    }

    private boolean isConnected(Section insertSection) {
        return (getUpEndStationId() == insertSection.getDownStationId())
                || (getDownEndStationId() == insertSection.getUpStationId());
    }

    public Optional<Section> findCollapsibleSection(Section insertSection) {
        return sections.stream()
                .filter(section -> section.isCollapsible(insertSection))
                .findAny();
    }

    public LineSections filterByStationId(Long stationId) {
        List<Section> sectionList = sections.stream()
                .filter(section -> section.containsStation(stationId))
                .collect(Collectors.toList());
        return new LineSections(sectionList);
    }

    public boolean hasSameSize(int size) {
        return sections.size() == size;
    }

    public List<Long> getSectionIds() {
        return sections.stream()
                .map(Section::getId)
                .collect(Collectors.toList());
    }

    public List<Long> getStationIds() {
        return sections.stream()
                .flatMap(section -> section.getStationIds().stream())
                .distinct()
                .collect(Collectors.toList());
    }
}
