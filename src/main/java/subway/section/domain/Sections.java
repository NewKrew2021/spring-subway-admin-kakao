package subway.section.domain;

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

    public boolean hasNoSections() {
        return sections.isEmpty();
    }

    public Section insertAndGetNewSection(Section upSection, Section downSection) {
        checkAreValidSections(upSection, downSection);

        final int NOT_DEFINED = Integer.MAX_VALUE;
        int distanceDiff = downSection.distanceDiff(upSection);

        Section existingSection = findSection(upSection);
        Section newSectionParameters = downSection;
        int newSectionDistance = NOT_DEFINED;
        if (!isExistingSection(existingSection)) {
            existingSection = findSection(downSection);
            newSectionParameters = upSection;
            newSectionDistance = existingSection.getDistance() - distanceDiff;
        }

        if (newSectionDistance == NOT_DEFINED) {
            newSectionDistance = existingSection.getDistance() + distanceDiff;
        }

        Section newSection = new Section(newSectionParameters.getLineID(),
                newSectionParameters.getStationID(),
                newSectionDistance);

        checkIsValidDistance(existingSection, newSection);
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

    protected void checkAreValidSections(Section upSection, Section downSection) {
        boolean upSectionIsExistingSection = isExistingSection(upSection);
        boolean downSectionIsExistingSection = isExistingSection(downSection);

        checkBothSectionsAlreadyExist(upSectionIsExistingSection, downSectionIsExistingSection);
        checkBothSectionsDoesNotExist(upSectionIsExistingSection, downSectionIsExistingSection);
    }

    private void checkBothSectionsAlreadyExist(boolean upSectionExists, boolean downSectionExists) {
        if (upSectionExists && downSectionExists) {
            throw new IllegalArgumentException("Cannot insert if both sections already exist");
        }
    }

    private void checkBothSectionsDoesNotExist(boolean upSectionExists, boolean downSectionExists) {
        if (!(upSectionExists || downSectionExists)) {
            throw new IllegalArgumentException("Cannot insert if neither section exist");
        }
    }

    private boolean isExistingSection(Section section) {
        return findSection(section) != null;
    }

    protected Section findSection(Section section) {
        return sections.stream()
                .filter(sec -> sec.equals(section))
                .findFirst()
                .orElse(null);
    }

    protected void checkIsValidDistance(Section existingSection, Section newSection) {
        Section nextSection = getNextSection(existingSection, newSection);
        if (isNotHighestOrLowestSection(nextSection) && existingSection.isFartherOrEqualFromThan(newSection, nextSection)) {
            throw new IllegalArgumentException("New section distance exceeds existing section distance");
        }
    }

    protected Section getNextSection(Section existingSection, Section newSection) {
        int nextSectionIdx = getNextSectionIdx(existingSection, newSection);
        boolean nextSectionIsInBounds = (0 <= nextSectionIdx) && (nextSectionIdx < sections.size());

        if (nextSectionIsInBounds) {
            return sections.get(nextSectionIdx);
        }

        return null;
    }

    protected int getNextSectionIdx(Section existingSection, Section newSection) {
        if (existingSection.isUpperThan(newSection)) {
            return sections.indexOf(existingSection) + 1;
        }

        return sections.indexOf(existingSection) - 1;
    }

    private boolean isNotHighestOrLowestSection(Section section) {
        return section != null;
    }

    private boolean hasMinimumSectionCount() {
        return sections.size() <= TWO_SECTIONS_REPRESENT_ONE;
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
