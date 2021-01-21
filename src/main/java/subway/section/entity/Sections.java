package subway.section.entity;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Sections {
    private final List<Section> sections;

    public Sections(List<Section> sections) {
        validate(sections);
        this.sections = Collections.unmodifiableList(
                sortInDownwardOrder(sections)
        );
    }

    private void validate(List<Section> sections) {
        if (isEmpty(sections)) {
            throw new IllegalArgumentException("비어있는 구간 리스트를 입력받을 수 없습니다.");
        }

        // 순환하지 않는다고 가정
        if (isNotUnited(sections)) {
            throw new IllegalArgumentException("한 줄기로 이어지지 않은 구간 리스트를 입력받을 수 없습니다.");
        }
    }

    private boolean isEmpty(List<Section> sections) {
        return sections == null || sections.isEmpty();
    }

    private boolean isNotUnited(List<Section> sections) {
        assert !isEmpty(sections);
        Map<Long, Section> cache = sections.stream()
                .collect(Collectors.toMap(
                        Section::getUpStationId, Function.identity()
                ));

        int count = 0;
        Section curr = cache.get(findUpEndStationId(sections));
        while (curr != null) {
            ++count;
            curr = cache.get(curr.getDownStationId());
        }
        return count != sections.size();
    }

    private List<Section> sortInDownwardOrder(List<Section> sections) {
        Map<Long, Section> cache = sections.stream()
                .collect(Collectors.toMap(
                        Section::getUpStationId, Function.identity()
                ));

        List<Section> sortedSections = new ArrayList<>();
        Section curr = cache.get(findUpEndStationId(sections));
        while (curr != null) {
            sortedSections.add(curr);
            curr = cache.get(curr.getDownStationId());
        }
        return sortedSections;
    }

    private long findUpEndStationId(List<Section> sections) {
        assert !sections.isEmpty();
        if (sections.size() == 1) {
            return sections.get(0).getUpStationId();
        }

        List<Long> stationIds = sections.stream()
                .flatMap(section -> section.getStationIds().stream())
                .collect(Collectors.toList());

        List<Long> endStationIds = stationIds.stream()
                .distinct()
                .filter(stationId -> Collections.frequency(stationIds, stationId) == 1)
                .collect(Collectors.toList());
        assert endStationIds.size() == 2; // upEnd, downEnd

        Map<Long, Section> cache = sections.stream()
                .collect(Collectors.toMap(
                        Section::getDownStationId, Function.identity()
                ));

        return cache.get(endStationIds.get(0)) == null ? endStationIds.get(0) : endStationIds.get(1);
    }

    public List<Long> getStationIdsInDownwardOrder() {
        List<Long> stationIds = new ArrayList<>();
        stationIds.add(sections.get(0).getUpStationId());
        for (Section section : sections) {
            stationIds.add(section.getDownStationId());
        }
        return stationIds;
    }

    public Section merge() {
        return new Section(
                getLineId(),
                getUpEndStationId(),
                getDownEndStationId(),
                getSumOfDistance()
        );
    }

    private Long getLineId() {
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

    public Stream<Section> stream() {
        return sections.stream();
    }

    public Sections filterByStationId(Long stationId) {
        return new Sections(
                sections.stream()
                        .filter(section -> section.containsStation(stationId))
                        .collect(Collectors.toList())
        );
    }

    public int size() {
        return sections.size();
    }

    public boolean isNotDeletable() {
        return sections.size() == 1;
    }

    public boolean isMultiple() {
        return sections.size() > 1;
    }

    private boolean isCircle(Section section) {
        return (getUpEndStationId() == section.getDownStationId())
                && (getDownEndStationId() == section.getUpStationId());
    }

    public boolean isExtendable(Section section) {
        return !isCircle(section) && (
                (getUpEndStationId() == section.getDownStationId())
                        || (getDownEndStationId() == section.getUpStationId())
        );
    }

    public Optional<Section> findCollapsibleSection(Section section) {
        return sections.stream()
                .filter(element -> element.isCollapsible(section))
                .findAny();
    }
}
