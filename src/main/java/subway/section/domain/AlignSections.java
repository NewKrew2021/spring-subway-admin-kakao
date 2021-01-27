package subway.section.domain;

import subway.exceptions.InvalidValueException;

import java.util.List;
import java.util.function.Consumer;

public class AlignSections {
    private List<Section> sections;

    public AlignSections(List<Section> sections) {
        this.sections = sections;
    }

    public void addSection(Section newSection) {
        checkDuplicatedSection(sections, newSection);
        checkSameStationContain(sections, newSection);

        alignSections(newSection);

        if (sections.size() == 0) {
            sections.add(newSection);
        }
    }

    private void alignSections(Section newSection) {
        int sectionLength = sections.size();
        for (int i = 0; i < sectionLength; i++) {
            Section savedSection = sections.get(i);
            if (savedSection.getUpStationId() == newSection.getUpStationId()) {
                if (savedSection.getDistance() <= newSection.getDistance()) {
                    throw new InvalidValueException();
                }
                savedSection.setUpStationId(newSection.getDownStationId());
                savedSection.setDistance(savedSection.getDistance() - newSection.getDistance());
            }
            if (savedSection.getDownStationId() == newSection.getDownStationId()) {
                if (savedSection.getDistance() <= newSection.getDistance()) {
                    throw new InvalidValueException();
                }
                savedSection.setDownStationId(newSection.getUpStationId());
                savedSection.setDistance(savedSection.getDistance() - newSection.getDistance());
            }
        }

        for (int i = 0; i < sectionLength; i++) {
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

    private void checkDuplicatedSection(List<Section> sections, Section newSection) {
        if (sections.stream().anyMatch(section ->
                section.getUpStationId() == newSection.getUpStationId() &&
                        section.getDownStationId() == newSection.getDownStationId()
        )) {
            throw new InvalidValueException();
        }
    }

    private void checkSameStationContain(List<Section> sections, Section newSection) {
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
