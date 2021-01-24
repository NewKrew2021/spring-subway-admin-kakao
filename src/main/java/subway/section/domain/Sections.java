package subway.section.domain;

import subway.section.vo.SectionResultValues;

import java.util.*;
import java.util.stream.Collectors;

public class Sections {
    private final int TWO_SECTIONS_REPRESENT_ONE = 2;
    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = Collections.unmodifiableList(sections);

        checkAreValidSections();
    }

    public List<Long> getStationIDs() {
        return sections.stream()
                .map(Section::getStationID)
                .collect(Collectors.toList());
    }

    public SectionResultValues toResultValues() {
        return new SectionResultValues(sections.stream()
                .map(Section::toResultValue)
                .collect(Collectors.toList()));
    }

    // TODO: 날씬해지자
    public Section createSection(Section upSectionParameters, Section downSectionParameters) {
        checkValidSections(upSectionParameters, downSectionParameters);

        final int NOT_DEFINED = Integer.MAX_VALUE;
        int distanceDiff = downSectionParameters.distanceDiff(upSectionParameters);

        Section existingSection = findSection(upSectionParameters);
        Section newSectionParameters = downSectionParameters;
        int newSectionDistance = NOT_DEFINED;
        if (!sectionExists(existingSection)) {
            existingSection = findSection(downSectionParameters);
            newSectionParameters = upSectionParameters;
            newSectionDistance = existingSection.getDistance() - distanceDiff;
        }

        if (newSectionDistance == NOT_DEFINED) {
            newSectionDistance = existingSection.getDistance() + distanceDiff;
        }

        Section newSection = new Section(newSectionParameters.getLineID(),
                newSectionParameters.getStationID(),
                newSectionDistance);

        // TODO: null 체크는 haveValidDistance 안에서 해보자
        if (!haveValidDistance(existingSection, newSection)) {
            return null;
        }

        return newSection;
    }

    public void checkIsDeletable(Section section) {
        if (hasMinimumSectionCount()) {
            throw new IllegalArgumentException("Cannot delete section when there are only two sections left");
        }

        Optional.ofNullable(findSection(section))
                .orElseThrow(() -> new NoSuchElementException(
                        String.format("Could not retrieve section with line id: %d and section id: %d",
                                section.getLineID(), section.getStationID())));
    }

    public boolean hasNoSections() {
        return sections.isEmpty();
    }

    protected void checkValidSections(Section upSection, Section downSection) {
        checkBothSectionsAlreadyExist(upSection, downSection);
        checkBothSectionsDoesNotExist(upSection, downSection);
    }

    private void checkBothSectionsAlreadyExist(Section upSection, Section downSection) {
        if (sectionExists(upSection) && sectionExists(downSection)) {
            throw new IllegalArgumentException("Cannot insert if both sections already exist");
        }
    }

    private void checkBothSectionsDoesNotExist(Section upSection, Section downSection) {
        if (!sectionExists(upSection) && !sectionExists(downSection)) {
            throw new IllegalArgumentException("Cannot insert if neither section exist");
        }
    }

    protected boolean haveValidDistance(Section existingSection, Section newSection) {
        Section nextSection = getNextSection(existingSection, newSection);
        if (isHighestOrLowestSection(nextSection)) {
            return true;
        }

        return existingSection.isCloserFromThan(newSection, nextSection);
    }

    protected Section getNextSection(Section existingSection, Section newSection) {
        try {
            return sections.get(getNextSectionIdx(existingSection, newSection));
        } catch (IndexOutOfBoundsException ignored) {
            return null;
        }
    }

    protected int getNextSectionIdx(Section existingSection, Section newSection) {
        if (existingSection.isUpperThan(newSection)) {
            return sections.indexOf(existingSection) + 1;
        }

        return sections.indexOf(existingSection) - 1;
    }

    protected Section findSection(Section section) {
        return sections.stream()
                .filter(sec -> sec.equals(section))
                .findFirst()
                .orElse(null);
    }

    private boolean hasMinimumSectionCount() {
        return sections.size() <= TWO_SECTIONS_REPRESENT_ONE;
    }

    private boolean sectionExists(Section sectionParameters) {
        return findSection(sectionParameters) != null;
    }

    private boolean isHighestOrLowestSection(Section section) {
        return section == null;
    }

    private void checkAreValidSections() {
        if (!isSorted()) {
            throw new IllegalArgumentException("Sections should be sorted by distance order");
        }

        if (hasDuplicates()) {
            throw new IllegalArgumentException("Sections cannot have duplicate elements");
        }
    }

    private boolean isSorted() {
        return sections.stream()
                .sorted(Comparator.comparingInt(Section::getDistance))
                .collect(Collectors.toList())
                .equals(sections);
    }

    private boolean hasDuplicates() {
        return sections.stream()
                .distinct()
                .count() != sections.size();
    }
}
