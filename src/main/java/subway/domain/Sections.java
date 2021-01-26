package subway.domain;

import subway.exception.SectionDistanceExceedException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Sections {
    private static final String DISTANCE_EXCEED_EXCEPTION_MESSAGE = "역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음";
    private static final String DUPLICATED_CREATE_EXCEPTION_MESSAGE = "상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음";
    private static final String NOT_CONTAINED_CREATE_EXCEPTION_MESSAGE = "상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음";
    public static final int FIRST = 0;
    public static final int SECOND = 1;
    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public Map<Long, Section> getUpStationKeyMap() {
        return sections.stream()
                .collect(Collectors.toMap(Section::getUpStationId, Function.identity()));
    }

    public Map<Long, Section> getDownStationKeyMap() {
        return sections.stream()
                .collect(Collectors.toMap(Section::getDownStationId, Function.identity()));
    }

    public Section getFirstSection() {
        return sections.stream()
                .filter(Section::isFirstSection)
                .findFirst()
                .get();
    }

    public Section getLastSection() {
        return sections.stream()
                .filter(Section::isLastSection)
                .findFirst()
                .get();
    }

    public Sections getContainedSections(Long stationId) {
        return new Sections(sections.stream()
                .filter(section -> section.getUpStationId().equals(stationId) || section.getDownStationId().equals(stationId))
                .collect(Collectors.toList()));
    }

    public Section getMergeSection(Long stationId) {
        return sections.get(FIRST).merge(sections.get(SECOND), stationId);
    }

    public Section getDeleteSection() {
        return sections.get(SECOND);
    }

    public Section getPreviousSection(Section lastSection) {
        Section previousSection = getDownStationKeyMap().get(lastSection.getUpStationId());
        previousSection.setLastSection(true);

        return previousSection;
    }

    public Section getNextSection(Section firstSection) {
        Section nextSection = getUpStationKeyMap().get(firstSection.getDownStationId());
        nextSection.setFirstSection(true);

        return nextSection;
    }

    public Sections getSeparatedSections(Section section) {
        validateDuplicate(section);

        Map<Long, Section> upStationKeyMap = getUpStationKeyMap();

        if (upStationKeyMap.containsKey(section.getUpStationId())) {
            Section targetSection = upStationKeyMap.get(section.getUpStationId());
            return separateDownStation(section, targetSection);
        }

        Map<Long, Section> downStationKeyMap = getDownStationKeyMap();

        if (downStationKeyMap.containsKey(section.getDownStationId())) {
            Section targetSection = downStationKeyMap.get(section.getDownStationId());
            return separateUpStation(section, targetSection);
        }

        throw new IllegalArgumentException(NOT_CONTAINED_CREATE_EXCEPTION_MESSAGE);
    }

    private Sections separateDownStation(Section section, Section targetSection) {
        validateDistance(section, targetSection);

        Section newSection = new Section(
                targetSection.getLineId(),
                section.getDownStationId(),
                targetSection.getDownStationId(),
                targetSection.getDistance() - section.getDistance(),
                Section.NOT_FIRST_SECTION,
                targetSection.isLastSection());

        Section updateSection = new Section(
                targetSection.getId(),
                targetSection.getLineId(),
                targetSection.getUpStationId(),
                section.getDownStationId(),
                section.getDistance(),
                targetSection.isFirstSection(),
                Section.NOT_LAST_SECTION);

        return new Sections(Arrays.asList(newSection, updateSection));
    }

    private Sections separateUpStation(Section section, Section targetSection) {
        validateDistance(section, targetSection);

        Section newSection = new Section(
                targetSection.getLineId(),
                section.getUpStationId(),
                targetSection.getDownStationId(),
                section.getDistance(),
                Section.NOT_FIRST_SECTION,
                targetSection.isLastSection());

        Section updateSection = new Section(
                targetSection.getId(),
                targetSection.getLineId(),
                targetSection.getUpStationId(),
                section.getUpStationId(),
                targetSection.getDistance() - section.getDistance(),
                targetSection.isFirstSection(),
                Section.NOT_LAST_SECTION);

        return new Sections(Arrays.asList(newSection, updateSection));
    }

    private void validateDuplicate(Section sectionRequest) {
        if (sections.stream()
                .filter(section -> section.getUpStationId().equals(sectionRequest.getUpStationId()))
                .anyMatch(section -> section.getDownStationId().equals(sectionRequest.getDownStationId()))) {
            throw new IllegalArgumentException(DUPLICATED_CREATE_EXCEPTION_MESSAGE);
        }
    }

    private void validateDistance(Section section, Section targetSection) {
        if (section.getDistance() >= targetSection.getDistance()) {
            throw new SectionDistanceExceedException(DISTANCE_EXCEED_EXCEPTION_MESSAGE);
        }
    }

    public Section getNewSection() {
        return sections.get(FIRST);
    }

    public Section getUpdateSection() {
        return sections.get(SECOND);
    }
}
