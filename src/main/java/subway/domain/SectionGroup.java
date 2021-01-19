package subway.domain;

import subway.exception.NoContentException;

import java.util.*;
import java.util.stream.IntStream;

import static subway.domain.Section.VIRTUAL_ENDPOINT_ID;

public class SectionGroup {

    private static final int NOT_FOUND = -1;

    private final List<Section> sections;

    public SectionGroup(List<Section> sections) {
        this.sections = sections;
    }

    public static SectionGroup insertFirstSection(Long lineId, Long upStationId, Long downStationId, int distance) {
        List<Section> sections = new ArrayList<>();
        sections.add(new Section(lineId, VIRTUAL_ENDPOINT_ID, upStationId, Integer.MAX_VALUE));
        sections.add(new Section(lineId, upStationId, downStationId, distance));
        sections.add(new Section(lineId, downStationId, VIRTUAL_ENDPOINT_ID, Integer.MAX_VALUE));
        return new SectionGroup(sections);
    }

    public Section insertSection(Long lineId, Long upStationId, Long downStationId, int distance) {
        int insertedIndex = findInsertedSectionIndex(upStationId, downStationId);

        if (distance >= sections.get(insertedIndex).getDistance()) {
            throw new IllegalArgumentException("기존 노선보다 작은 길이를 입력해야 합니다.");
        }

        return new Section(lineId, upStationId, downStationId, distance);
    }

    private int findInsertedSectionIndex(Long upStationId, Long downStationId) {
        int upIndex = findSectionIndexWithUpStation(upStationId).orElse(NOT_FOUND);
        int downIndex = findSectionIndexWithDownStation(downStationId).orElse(NOT_FOUND);

        if ((upIndex == NOT_FOUND) == (downIndex == NOT_FOUND)) {
            throw new IllegalArgumentException("두 역이 모두 없거나 있으면 안됩니다.");
        }

        return (upIndex * downIndex) / NOT_FOUND;
    }

    public Section divideSection(Section insertedSection) {
        int dividedIndex = findInsertedSectionIndex(insertedSection.getUpStationId(), insertedSection.getDownStationId());
        Section present = sections.get(dividedIndex);
        int dividedDistance = present.getDistance() - insertedSection.getDistance();

        if (present.shareUpStation(insertedSection)) {
            return new Section(present.getId(), present.getLineId(), insertedSection.getDownStationId(), present.getDownStationId(), dividedDistance);
        }

        return new Section(present.getId(), present.getLineId(), present.getUpStationId(), insertedSection.getUpStationId(), dividedDistance);
    }


    public Section deleteStation(Long stationId) {
        if (sections.size() <= 3) {
            throw new IllegalArgumentException("구간이 한 개 이하이면 삭제할 수 없습니다.");
        }
        int deletedIndex = findSectionIndexWithUpStation(stationId)
                .orElseThrow(() -> new NoContentException("삭제하려는 역이 없습니다."));

        return sections.get(deletedIndex);
    }

    public Section combineSection(Section deletedSection) {
        int upperSectionIndex = findSectionIndexWithDownStation(deletedSection.getUpStationId())
                .orElseThrow(() -> new NoContentException("삭제하려는 역이 없습니다."));

        Section present = sections.get(upperSectionIndex);

        int combinedDistance = present.getDistance() + deletedSection.getDistance();

        return new Section(present.getId(), present.getLineId(), present.getUpStationId(), deletedSection.getDownStationId(), combinedDistance);
    }

    private OptionalInt findSectionIndexWithUpStation(Long stationId) {
        return IntStream.range(0, sections.size())
                .filter(i -> sections.get(i).isUpStation(stationId))
                .findAny();
    }

    private OptionalInt findSectionIndexWithDownStation(Long stationId) {
        return IntStream.range(0, sections.size())
                .filter(i -> sections.get(i).isDownStation(stationId))
                .findAny();
    }

    public List<Section> getSections() {
        return sections;
    }

    public List<Long> getAllStationId() {
        Map<Long, Long> chain = new HashMap<>();
        sections.forEach(section -> chain.put(section.getUpStationId(),
                section.getDownStationId()));

        List<Long> stationIds = new ArrayList<>();
        for (Long upStationId = chain.get(VIRTUAL_ENDPOINT_ID); !upStationId.equals(VIRTUAL_ENDPOINT_ID); upStationId = chain.get(upStationId)) {
            stationIds.add(upStationId);
        }
        return stationIds;
        /* 이유를 알 수 없지만 Stream의 iterate를 사용한 아래의 코드가 동작하지 않습니다.
        return Stream.iterate(chain.get(VIRTUAL_ENDPOINT_ID),
                (n -> !n.equals(VIRTUAL_ENDPOINT_ID)),
                chain::get)
                .collect(Collectors.toList());
         */
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }
}
