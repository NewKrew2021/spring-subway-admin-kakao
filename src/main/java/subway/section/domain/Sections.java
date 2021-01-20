package subway.section.domain;

import java.util.Arrays;
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
    private static final int INITIAL_DEFAULT_POSITION = 0;

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

    public static Sections initialize(SectionCreateValue sectionValue) {
        return Sections.from(
                Arrays.asList(
                        new Section(sectionValue.getLineId(), sectionValue.getUpStationId(), INITIAL_DEFAULT_POSITION),
                        new Section(sectionValue.getLineId(), sectionValue.getDownStationId(), sectionValue.getDistance() + INITIAL_DEFAULT_POSITION)
                )
        );
    }

    public Section createSection(SectionCreateValue createValue) {
        Optional<Section> newDownSideSection = createSection(DownSideSectionGenerator.from(createValue, getLastIndex()));
        Optional<Section> newUpSideSection = createSection(UpSideSectionGenerator.from(createValue));
        if ((newDownSideSection.isPresent() && newUpSideSection.isPresent())
                || (!newDownSideSection.isPresent() && !newUpSideSection.isPresent())) {
            throw new IllegalArgumentException(UP_OR_DOWN_ONLY_ONE_EXCEPTION_MESSAGE);
        }

        return newDownSideSection.orElseGet(newUpSideSection::get);
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

    private int getIndexOf(Section section) {
        return sections.stream()
                .filter(section::equals)
                .map(sections::indexOf)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("일치하는 구간이 없습니다"));
    }

    private Optional<Section> createSection(SectionGenerateStrategy strategy) {
        return findSectionByStation(strategy.getStartStationId())
                .map(section -> createNextSectionOf(section, strategy));
    }

    private Section createNextSectionOf(Section section, SectionGenerateStrategy strategy) {
        int idx = getIndexOf(section);
        if (strategy.isNotTerminalIndex(idx)
                && getNextSection(strategy, idx).getDifferenceOfPosition(section) <= strategy.getDistance()) {
            throw new IllegalArgumentException(DISTANCE_INVALID_EXCEPTION_MESSAGE);
        }
        return strategy.createFrom(section);
    }

    private Section getNextSection(SectionGenerateStrategy strategy, int idx) {
        return sections.get(strategy.getNextIndexOf(idx));
    }

    private boolean isNotRemovable() {
        return sections.size() == INITIAL_SIZE;
    }

    private int getLastIndex() {
        return sections.size() - 1;
    }

    public List<Section> getSections() {
        return sections;
    }
}
