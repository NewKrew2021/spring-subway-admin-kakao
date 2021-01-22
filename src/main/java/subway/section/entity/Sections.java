package subway.section.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Sections {
    private final List<Section> sections;

    public Sections(List<Section> sections) {
        validate(sections);
        this.sections = Collections.unmodifiableList(sections);
    }

    private void validate(List<Section> sections) {
        if (isEmpty(sections)) {
            throw new IllegalArgumentException("비어있는 구간 리스트를 입력받을 수 없습니다.");
        }
    }

    private boolean isEmpty(List<Section> sections) {
        return sections == null || sections.isEmpty();
    }

    public LineSections getLineSections() {
        Section curr = getUpEndSection();
        Map<Long, Section> identityMap = getIdentityMap();

        List<Section> sortedSections = new ArrayList<>();
        while (curr != null) {
            sortedSections.add(curr);
            curr = identityMap.get(curr.getDownStationId());
        }

        if (isNotEqual(sortedSections)) {
            throw new IllegalStateException("끊어진 구간 리스트는 노선 구간이 될 수 없습니다.");
        }
        return new LineSections(sortedSections);
    }

    private Section getUpEndSection() {
        List<Long> endStationIds = getEndStationIds();
        assert endStationIds.size() == 2;
//        return sections.stream()
//                .filter(section -> endStationIds.contains(section.getUpStationId()))
//                .collect(Collectors.collectingAndThen(
//                        Collectors.toList(),
//                        list -> {
//                            assert (list != null && list.size() == 1);
//                            return list.get(0);
//                        }
//                ));
        return sections.stream()
                .filter(section -> endStationIds.contains(section.getUpStationId()))
                .findAny()
                .orElse(null);
    }

    private List<Long> getEndStationIds() {
        return sections.stream()
                .flatMap(section -> section.getStationIds().stream())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() == 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private Map<Long, Section> getIdentityMap() {
        return sections.stream()
                .collect(Collectors.toMap(
                        Section::getUpStationId, Function.identity()
                ));
    }

    private boolean isNotEqual(List<Section> sortedSections) {
        return (sections.size() != sortedSections.size())
                || (!sortedSections.containsAll(sections));
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

    public boolean hasSameSize(int size) {
        return sections.size() == size;
    }
}
