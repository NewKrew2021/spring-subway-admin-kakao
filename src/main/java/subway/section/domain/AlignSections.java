package subway.section.domain;

import subway.exceptions.InvalidValueException;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class AlignSections {
    private List<Section> sections;

    public AlignSections(List<Section> sections) {
        this.sections = sections;
    }

    public void addSection(Section newSection) {
        checkDuplicatedSection(newSection);
        checkSameStationContain(newSection);
        checkSectionIsOverDistance(newSection);

        alignSections(newSection);

        if (sections.size() == 0) {
            sections.add(newSection);
        }
    }

    private void alignSections(Section newSection) {
        if (isOverlappedSectionExist(newSection)) {
            Section overlappedSection = findOverlappedSection(newSection);
            overlappedSection.splitBy(newSection);
        }
        insertSectionToRightPosition(newSection);
    }

    private boolean isOverlappedSectionExist(Section section) {
        return sections.stream()
                .anyMatch((Section saved) -> saved.isInclude(section));
    }

    private Section findOverlappedSection(Section section) {
        return sections.stream()
                .filter((Section saved) -> saved.isInclude(section))
                .collect(Collectors.toList())
                .get(0);
    }

    private void insertSectionToRightPosition(Section newSection) {
        for (int i = 0; i < sections.size(); i++) {
            if (sections.get(i).getUpStationId() == newSection.getDownStationId()) {
                sections.add(i, newSection);
                break;
            }
            if (sections.get(i).getDownStationId() == newSection.getUpStationId()) {
                sections.add(i + 1, newSection);
                break;
            }
        }
    }

    private void checkSectionIsOverDistance(Section newSection) {
        sections.stream()
                .forEach(savedSection -> {
                    if (savedSection.isIncludeAndOverDistance(newSection)) {
                        throw new InvalidValueException();
                    }
                });
    }

    private void checkDuplicatedSection(Section newSection) {
        if (sections.stream().anyMatch(section ->
                section.getUpStationId() == newSection.getUpStationId() &&
                        section.getDownStationId() == newSection.getDownStationId()
        )) {
            throw new InvalidValueException();
        }
    }

    private void checkSameStationContain(Section newSection) {
        if (sections.size() > 0 && sections.stream().allMatch(section ->
                section.getUpStationId() != newSection.getUpStationId() &&
                        section.getUpStationId() != newSection.getDownStationId() &&
                        section.getDownStationId() != newSection.getUpStationId() &&
                        section.getDownStationId() != newSection.getDownStationId()
        )) {
            throw new InvalidValueException();
        }
    }

    public void applyToAllSection(Consumer<Section> callback) {
        sections.stream().forEach((Section section) -> {
            callback.accept(section);
        });
    }

    public Section findByStationId(Long upStationId, Long downStationId) {
        for (Section section : sections) {
            if (section.getUpStationId() == upStationId && section.getDownStationId() == downStationId) {
                return section;
            }
        }

        return null;
    }

}
