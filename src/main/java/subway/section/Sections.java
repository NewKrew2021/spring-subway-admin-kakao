package subway.section;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

public class Sections {

    private static final int MIN_SECTIONS_SIZE = 3;
    private static final String MIN_SECTIONS_SIZE_EXCEPTION_MESSAGE = "구간은 최소 3개로 이루어져야 합니다.";
    private static final String TERMINAL_INCLUDE_EXCEPTION_MESSAGE = "상/하행 종점이 모두 포함되어야 합니다.";
    private static final String UP_TERMINAL_NOT_EXIST_EXCEPTION_MESSAGE = "상행 종점이 존재하지 않습니다.";
    private static final String NOT_MATCHED_STATION_EXCEPTION_MESSAGE = "상/하행 중 일치하는 역이 없습니다.";
    private static final String NOT_CONTAINED_STATION_EXCEPTION_MESSAGE = "구간에 포함되지 않은 지하철역 입니다.";

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        validate(sections);

        this.sections = Collections.unmodifiableList(sections);
    }

    private void validate(List<Section> sections) {
        if (sections.size() < MIN_SECTIONS_SIZE) {
            throw new IllegalArgumentException(MIN_SECTIONS_SIZE_EXCEPTION_MESSAGE);
        }

        if (notHaveUpTerminal(sections) || notHaveDownTerminal(sections)) {
            throw new IllegalArgumentException(TERMINAL_INCLUDE_EXCEPTION_MESSAGE);
        }
    }

    private boolean notHaveUpTerminal(List<Section> sections) {
        return sections.stream()
                .noneMatch(Section::isUpTerminal);
    }

    private boolean notHaveDownTerminal(List<Section> sections) {
        return sections.stream()
                .noneMatch(Section::isDownTerminal);
    }

    public List<Long> getSortedStationIds() {
        Map<Long, Section> sectionCache = sections.stream()
                .collect(Collectors.toMap(Section::getUpStationId, Function.identity()));

        List<Long> stationIds = new ArrayList<>();
        Section section = findUpTerminalSection();
        while (!section.isDownTerminal()) {
            long nextStationId = section.getDownStationId();
            stationIds.add(nextStationId);
            section = sectionCache.get(nextStationId);
        }
        return stationIds;
    }

    private Section findUpTerminalSection() {
        return sections.stream()
                .filter(Section::isUpTerminal)
                .findFirst()
                .orElseThrow(() -> new AssertionError(UP_TERMINAL_NOT_EXIST_EXCEPTION_MESSAGE));
    }

    public Section findBySameUpOrDownStationWith(Section section) {
        return sections.stream()
                .filter(it -> it.hasSameUpStation(section) || it.hasSameDownStation(section))
                .findAny()
                .orElseThrow(() -> new IllegalStateException(NOT_MATCHED_STATION_EXCEPTION_MESSAGE));
    }

    public boolean isInitialState() {
        return sections.size() == MIN_SECTIONS_SIZE;
    }

    public Pair<Section, Section> findByStationId(Long stationId) {
        try {
            return sections.stream()
                    .filter(section -> section.containsStation(stationId))
                    .collect(collectingAndThen(toList(), l -> new ImmutablePair<>(l.get(0), l.get(1))));
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException(NOT_CONTAINED_STATION_EXCEPTION_MESSAGE);
        }
    }
}
