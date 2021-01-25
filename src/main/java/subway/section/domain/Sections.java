package subway.section.domain;

import subway.section.vo.SectionCreateValue;

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

    public Section getNewSectionIfValid(SectionCreateValue createValue) {
        checkAreValidStationIDs(createValue);

        Section existingSection = getExistingSection(createValue);
        Section newSection = getNewSection(createValue);

        checkIsValidDistance(existingSection, newSection);
        return newSection;
    }

    private Section getExistingSection(SectionCreateValue createValue) {
        Section section = findSectionBy(createValue.getUpStationID());
        if (sectionExists(section)) {
            return section;
        }

        return findSectionBy(createValue.getDownStationID());
    }

    private Section getNewSection(SectionCreateValue createValue) {
        if (isNotExistingSection(createValue.getUpStationID())) {
            Section downSection = findSectionBy(createValue.getDownStationID());

            return new Section(createValue.getLineID(), createValue.getUpStationID(),
                    downSection.getDistance() - createValue.getDistanceDiff());
        }

        Section upSection = findSectionBy(createValue.getUpStationID());
        return new Section(createValue.getLineID(), createValue.getDownStationID(),
                upSection.getDistance() + createValue.getDistanceDiff());
    }

    private boolean sectionExists(Section section) {
        return section != null;
    }

    public void checkIsDeletable(Section section) {
        if (hasMinimumSectionCount()) {
            throw new IllegalArgumentException("Cannot delete section when there are only two sections left");
        }

        Optional.ofNullable(findSectionBy(section.getStationID()))
                .orElseThrow(() -> new NoSuchElementException(
                        String.format("Could not retrieve section with line id: %d and section id: %d",
                                section.getLineID(), section.getStationID())));
    }

    protected void checkAreValidStationIDs(SectionCreateValue createValue) {
        boolean upSectionIsExistingSection = isExistingSection(createValue.getUpStationID());
        boolean downSectionIsExistingSection = isExistingSection(createValue.getDownStationID());

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

    private boolean isExistingSection(long stationID) {
        return findSectionBy(stationID) != null;
    }

    private boolean isNotExistingSection(long stationID) {
        return findSectionBy(stationID) == null;
    }

    protected Section findSectionBy(long stationID) {
        return sections.stream()
                .filter(section -> section.getStationID() == stationID)
                .findFirst()
                .orElse(null);
    }

    protected void checkIsValidDistance(Section existingSection, Section newSection) {
        Section nextSection = getNextSection(existingSection, newSection);
        if (isTerminalSection(nextSection) && existingSection.isFartherOrEqualFromThan(newSection, nextSection)) {
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

    private boolean isTerminalSection(Section section) {
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

        if (hasDifferentLineIDs()) {
            throw new IllegalArgumentException("Sections cannot have different line IDs");
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

    private boolean hasDifferentLineIDs() {
        return sections.stream()
                .mapToLong(Section::getLineID)
                .distinct()
                .count() > 1;
    }
}
