package subway.section;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public class Sections {

    private static final String MINIMUM_SECTION_STATION_EXCEPTION_MESSAGE = "구간에 포함되는 역의 갯수는 두개 이상이어야 합니다";
    private static final String DISTANCE_INVALID_EXCEPTION_MESSAGE = "기존 구간보다 새로 생긴 구간의 거리가 더 짧아야합니다";
    private static final String UP_OR_DOWN_ONLY_ONE_EXCEPTION_MESSAGE = "상/하행역 중 하나만 일치해야합니다";
    private static final String CANNOT_REMOVE_INITIAL_SECTIONS_EXCEPTION_MESSAGE = "해당 노선은 지하철역을 삭제할 수 없습니다";

    private static final int INITIAL_SIZE = 2;
    private static final int NEXT_INDEX = 1;

    private final List<Section> sections;

    private Sections(List<Section> sections) {
        validateSize(sections);

        this.sections = Collections.unmodifiableList(sections);
    }

    private void validateSize(List<Section> sections) {
        if (sections.size() < INITIAL_SIZE) {
            throw new IllegalArgumentException(MINIMUM_SECTION_STATION_EXCEPTION_MESSAGE);
        }
    }

    public static Sections from(List<Section> sections) {
        return new Sections(sections);
    }

    public Section createNewSection(long upStationId, long downStationId, int distance) {
        Optional<Section> upSection = findSectionByStation(upStationId);
        Optional<Section> downSection = findSectionByStation(downStationId);
        if ((upSection.isPresent() && downSection.isPresent())
                || (!upSection.isPresent() && !downSection.isPresent())) {
            throw new IllegalArgumentException(UP_OR_DOWN_ONLY_ONE_EXCEPTION_MESSAGE);
        }

        return upSection.map(section ->
                createNextDownSectionOf(section, downStationId, distance)
        ).orElseGet(() ->
                createNextUpSectionOf(downSection.get(), upStationId, distance)
        );
    }

    public List<Long> getStations() {
        return sections.stream()
                .map(Section::getStationId)
                .collect(toList());
    }

    public Optional<Section> findSectionToDeleteBy(long stationId) {
        if (isNotRemovable()) {
            throw new IllegalStateException(CANNOT_REMOVE_INITIAL_SECTIONS_EXCEPTION_MESSAGE);
        }

        return findSectionByStation(stationId);
    }

    private Optional<Section> findSectionByStation(long stationId) {
        return sections.stream()
                .filter(section -> section.hasStation(stationId))
                .findAny();
    }

    private int findIndexOf(Section section) {
        return sections.stream()
                .filter(section::equals)
                .map(sections::indexOf)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("일치하는 구간이 없습니다"));
    }

    private Section createNextDownSectionOf(Section section, Long stationId, int distance) {
        if (!isDownTerminal(section) && getNextDownSection(section).getDifferenceOfPosition(section) <= distance) {
            throw new IllegalArgumentException(DISTANCE_INVALID_EXCEPTION_MESSAGE);
        }
        return new Section(section.getLineId(), stationId, section.calculateNextDownPosition(distance));
    }

    private Section createNextUpSectionOf(Section section, Long stationId, int distance) {
        if (!isUpTerminal(section) && getNextUpSection(section).getDifferenceOfPosition(section) <= distance) {
            throw new IllegalArgumentException(DISTANCE_INVALID_EXCEPTION_MESSAGE);
        }
        return new Section(section.getLineId(), stationId, section.calculateNextUpPosition(distance));
    }

    private boolean isNotRemovable() {
        return sections.size() == INITIAL_SIZE;
    }

    private boolean isUpTerminal(Section section) {
        return findIndexOf(section) == 0;
    }

    private boolean isDownTerminal(Section section) {
        return findIndexOf(section) == sections.size() - 1;
    }

    private Section getNextUpSection(Section section) {
        if (isUpTerminal(section)) {
            throw new IllegalArgumentException("해당 구간은 상행 종점입니다");
        }
        return sections.get(findIndexOf(section) - NEXT_INDEX);
    }

    private Section getNextDownSection(Section section) {
        if (isDownTerminal(section)) {
            throw new IllegalArgumentException("해당 구간은 하행 종점입니다");
        }
        return sections.get(findIndexOf(section) + NEXT_INDEX);
    }
}
