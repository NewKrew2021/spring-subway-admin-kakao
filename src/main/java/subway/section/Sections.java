package subway.section;

import subway.exceptions.InvalidSectionException;

import java.util.ArrayList;
import java.util.List;

public class Sections {

    public static final String NO_MATCHING_SECTION_ERROR_MESSAGE = "구간이 존재하지 않습니다.";
    public static final int MINIMUM_SECTION_SIZE = 1;

    private final List<Section> sections = new ArrayList<>();

    public Sections(List<Section> sections, Long startStationId) {
        Section startSection = sections.stream()
                .filter(section -> section.getUpStationId() == startStationId)
                .findFirst()
                .orElseThrow(() -> new InvalidSectionException(NO_MATCHING_SECTION_ERROR_MESSAGE));
        this.sections.add(startSection);
        while (this.sections.size() < sections.size()) {
            addSectionAtLast(sections);
        }
    }

    private void addSectionAtLast(List<Section> sections) {
        Section findSection = sections.stream()
                .filter(section -> section.getUpStationId() == getLastStation())
                .findFirst()
                .orElseThrow(() -> new InvalidSectionException(NO_MATCHING_SECTION_ERROR_MESSAGE));
        this.sections.add(findSection);
    }

    private Long getLastStation() {
        return sections.get(sections.size() - 1).getDownStationId();
    }

    public int getSize() {
        return sections.size();
    }

    public Section findByUpStationId(Long stationId) {
        Section resultSection = sections.stream()
                .filter(section -> section.getUpStationId() == stationId)
                .findFirst()
                .orElseThrow(() -> new InvalidSectionException(NO_MATCHING_SECTION_ERROR_MESSAGE));
        return resultSection;
    }

    public Section findByDownStationId(Long stationId) {
        Section resultSection = sections.stream()
                .filter(section -> section.getDownStationId() == stationId)
                .findFirst()
                .orElseThrow(() -> new InvalidSectionException(NO_MATCHING_SECTION_ERROR_MESSAGE));
        return resultSection;
    }

    public boolean isContainsBothStationsOrNothing(Long upStationId, Long downStationId) {
        long upStationCount = sections.stream()
                .filter(section -> (section.getUpStationId() == upStationId || section.getDownStationId() == upStationId))
                .count();
        long downStationCount = sections.stream()
                .filter(section -> (section.getDownStationId() == downStationId || section.getUpStationId() == downStationId))
                .count();
        return ((upStationCount > 0) == (downStationCount > 0));
    }

    public Section findUpdatedSection(Section newSection) {
        Section currentSection;
        try {
            currentSection = findByUpStationId(newSection.getUpStationId());
        } catch (InvalidSectionException e) {
            currentSection = findByDownStationId(newSection.getDownStationId());
        }
        currentSection.updateSectionInfoWhenInserted(newSection);
        return currentSection;
    }

    public List<Long> getStationsSortedSequence() {
        List<Long> sequence = new ArrayList<>();
        for (Section section : sections) {
            sequence.add(section.getUpStationId());
        }
        sequence.add(getLastStation());
        return sequence;
    }

    public Section getFirstSection() {
        return sections.get(0);
    }

    public Section getLastSection() {
        return sections.get(sections.size() - 1);
    }

    public boolean isRemovable() {
        return sections.size() > MINIMUM_SECTION_SIZE;
    }
}